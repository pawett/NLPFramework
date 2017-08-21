package com.NLPFramework.Helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Files.NLPFile;
import com.NLPFramework.Files.PlainFile;
import com.NLPFramework.Files.XMLFile;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Processor.Configuration;
import com.NLPFramework.Processor.TMLExtractor;


public class FileConverter {

     
    public static String ConvertTextToFile(String input_text) throws IOException {
		// Convert input text to a file if necessary
		if (input_text != null && input_text.length() > 0) {
			System.err.println("TIPSem text: " + input_text);
			// Save text to a default file
			//String tmpfile = FileUtils.getApplicationPath() + "program-data/tmp/tmp" + dateFormat.format(ExecTime);
			final DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd-HH.mm.ss.SSS");
			String tmpfile = "tmp" + dateFormat.format(new Date());
			BufferedWriter outfile = new BufferedWriter(new FileWriter(tmpfile));
			try {
				outfile.write(input_text + "\n");
			} finally {

				if (outfile != null) {
					outfile.close();
				}
				/*input_files = new String[1];
				input_files[0] = tmpfile;*/
				
			}
			return tmpfile;
		}
		return null;
	}
    
    /**
     * Obtains the dataset of a directory containing tml files.
     * @param basedir
     * @param approach
     */
    //TODO: This must return an TokenizedFileHashtable. This way it is possible to get rid of the awful TokenizedHashtable constructor, by saving the class as binary
    public static TokenizedFileHashtable tmldir2features(File basedir, String approach)
    {
        String featuresdir = basedir.getParent() + File.separator + basedir.getName() + "_" + approach + "_features" + File.separator;
        try 
        {
           /* if ((new File(featuresdir + "base-segmentation.TempEval2-features").exists())) {
                throw new Exception("PREVENTIVE ERROR: Save or delete the previousely generated features because these will be overwritten after this process: " + featuresdir);
            }*/

            File ftdir = new File(featuresdir);
            if (ftdir.exists()) {
                FileUtils.deleteRecursively(ftdir);
            }
            if (!ftdir.mkdirs()) {  // mkdirs creates many parent dirs if needed
                throw new Exception("Directory not created...");
            }

            File[] tmlfiles = basedir.listFiles(FileUtils.onlyFilesFilter);
            File[] tmldirs = basedir.listFiles(FileUtils.onlyDirsNonAuxDirs);

            for (int i = 0; i < tmldirs.length; i++) {
                tmlfiles = StringUtils.concatArray(tmlfiles, tmldirs[i].listFiles(FileUtils.onlyFilesFilter));
            }

           List<File> files = Arrays.asList(tmlfiles);
           ArrayList<TokenizedFile> filesAnnotated = new ArrayList<>();
           files.parallelStream().forEach((tmlFile) -> {
        	   try {
        		   TokenizedFile file = processFile(approach, Configuration.getLanguage(), featuresdir, tmlFile);
        		   if(file != null)
        			   filesAnnotated.add(file);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
           });
           
           TokenizedFileHashtable hashtableFiles = new TokenizedFileHashtable();
           
           for(TokenizedFile f : filesAnnotated)
			{
        	   hashtableFiles.put(f.getName(), f);
			}
         
           return hashtableFiles;


        } catch (Exception e) 
        {
        	Logger.WriteError("Errors found (FileConverter):\n\t" ,e);
        	System.exit(1);
        }
        
        return null;

    }

	private static TokenizedFile processFile(String approach, Language lang, String featuresdir, File tmlfile)
			throws Exception, IOException 
	{
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("File: " + tmlfile.getAbsolutePath());
		}
		NLPFile nlpfile = new PlainFile(tmlfile.getAbsolutePath());
		nlpfile.setLanguage(lang);
		if (!(FileUtils.getNLPFormat(nlpfile.getFile())).equalsIgnoreCase("XML")) {
		   // throw new Exception("TimeML (.tml) XML file is required as input. Found: " + nlpfile.getFile().getCanonicalPath());
			return  null;
		}

		XMLFile xmlfile = new XMLFile(nlpfile.getFile().getAbsolutePath(),null);
		xmlfile.setLanguage(lang);
		if (!xmlfile.getExtension().equalsIgnoreCase("tml")) {
		    throw new Exception("TimeML (.tml) XML file is required as input.");
		}

		if (!xmlfile.isWellFormatted()) {
		    throw new Exception("File: " + xmlfile.getFile() + " is not a valid TimeML (.tml) XML file.");
		}

		// Create a working directory (commented because that way we can reuse roth-freeling annotations)
		File dir = new File(nlpfile.getFile().getCanonicalPath() + "-" + approach + "_features/");
		if (!dir.exists() || !dir.isDirectory()) {
		    dir.mkdir();
		}
		// Copy the valid TML-XML file
		String output = dir + File.separator + nlpfile.getFile().getName();
		FileUtils.copyFileUtil(nlpfile.getFile(), new File(output));
		xmlfile = new XMLFile(output,null);

		
		TokenizedFile f = TMLExtractor.getAnnotationsFromTML(xmlfile.getFile().getCanonicalPath(), lang);
		FeaturesFormatter format = new FeaturesFormatter();
		TokenFileFormatter formatter = new TokenFileFormatter(f);
		formatter.toFile(dir.getAbsolutePath(), format);
		return f;
	}



    
   }
