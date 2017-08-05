package com.NLPFramework.externalTools;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Formatters.IWordFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;

/**
 * REGULAR INSTALLATION AND INCLUSION IN PATH REQUIRED
 * @author Hector Llorens
 * @since 2011
 */
public class SVM implements IMachineLearningMethod {

    // path is not necessary but is used to capture temporal files (if there are), or default templates
    //public static String program_path = FileUtils.getApplicationPath() + "program-data/SVM/";
	 public static String program_path = "/home/pawett/NLPFramework/Processor/program-data/SVM/";
    private final String modelFileExtension = ".SVMmodel";

    private String getTemplate(String templatefile) throws Exception 
	{
		File tempf = new File(templatefile);
		if (!tempf.exists() || !tempf.isFile()) {
		    tempf = new File(program_path + templatefile);
		    if (!tempf.exists() || !tempf.isFile()) {
		        tempf = new File(program_path + "templates/" + templatefile);
		        if (!tempf.exists() || !tempf.isFile()) {
		            throw new Exception("Template file (" + templatefile + ") not found.");
		        } else {
		            templatefile = program_path + "templates/" + templatefile;
		        }
		    } else {
		        templatefile = program_path + templatefile;
		    }
		}
		return templatefile;
	}
    
   
    
    public String test(String featuresfile, String modelFile)
    {
        int folderposition=modelFile.lastIndexOf('/');
        String outputfile = featuresfile + "-annotatedWith-SVMmodel-" + modelFile.substring(folderposition+1, modelFile.lastIndexOf('.'));
        try 
        {
        	modelFile = getTemplate(modelFile);
        	MachineLearningHelper.CreateInputFormat(program_path, featuresfile);
           	ExecuteSVM(modelFile);
        	MachineLearningHelper.CreateOutput(program_path, outputfile);
        	MachineLearningHelper.ClearTmp(program_path);
          
        } catch (Exception e) 
        {
           Logger.WriteError("Errors found (SVM):\n\t", e);
           return null;
        }
        
        return outputfile;
    }
    /**
     * Runs SVM over a features file
     * and saves a model in a .SVMmodel file
     *
     * Format 
     *
     * @param filename
     * @return Output filename
     */
    public String Train(String featuresfile, String templateFile) {
        String outputfile = featuresfile +"."+ templateFile.substring(0,templateFile.lastIndexOf('.')) +".SVMmodel";
        try 
        {
        	templateFile = getTemplate(templateFile);
            Process p;
            
            BufferedReader stdInput;
            MachineLearningHelper.CreateInputFormat(program_path, featuresfile);

            String[] command2 = {"/bin/sh","-c","sh "+program_path +"myTrain.sh "+templateFile+" "+program_path + "temp.tmp"+" "+outputfile};
            p=Runtime.getRuntime().exec(command2);
            
            stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                String line;
                while ((line = stdInput.readLine()) != null) {
                    System.err.println(line);
                }
            } finally {
                if (stdInput != null) {
                    stdInput.close();
                }
                if(p!=null){
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                    p.destroy();
                }
            }

            MachineLearningHelper.ClearTmp(program_path);

        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (SVM):", e);
            return null;
        }
        return outputfile;
    }


    public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, IWordFormatter fromFormatter, IWordFormatter toFormatter) 
    {
    	String modelFile =  models_path + File.separator + "SVM" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition=modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
        String outputfile = featuresfilePath + "-annotatedWith-SVMmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));
      
       // TokenizedFile file = new TokenizedFile(returnType, Languages.valueOf(lang.toUpperCase()), tokenizedFile.getName());
        try 
        {
        	modelFile = getTemplate(modelFile);
        
        	MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteSVM(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);
            
            UpdateTokenizedFile(formatter, toFormatter, outputfile);


        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (SVM):\n\t", e);
            return;
        }
    }



	private void UpdateTokenizedFile(TokenFileFormatter formatter, IWordFormatter format, String outputfile)
			throws FileNotFoundException, IOException
	{
	//	tokenizedFile.setType(returnType);

		BufferedReader fileReader = new BufferedReader(new FileReader(outputfile));
		try {
			String line="";
			while ((line = fileReader.readLine()) != null)
			{
				formatter.updateFromPipes(line, format);
			}
		} finally 
		{
			if (fileReader != null) 
			{
				fileReader.close();
			}

		}
	}
	
	private void UpdateTokenizedFile(TokenFileFormatter formatter, IFileFormatter format, String outputfile)
			throws FileNotFoundException, IOException
	{
	//	tokenizedFile.setType(returnType);

		BufferedReader fileReader = new BufferedReader(new FileReader(outputfile));
		try {
			String line="";
			while ((line = fileReader.readLine()) != null)
			{
				formatter.updateFromPipes(line, format);
			}
		} finally 
		{
			if (fileReader != null) 
			{
				fileReader.close();
			}

		}
	}

	private void ExecuteSVM(String modelfile) throws IOException {
		Process p;
		BufferedReader stdInput;
		String[] command2 = {"/bin/sh","-c","yamcha -m "+modelfile+" < "+program_path + "temp.tmp > "+program_path + "temp2.tmp"};
		p=Runtime.getRuntime().exec(command2);
		
		stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		try {
		    String line;
		    while ((line = stdInput.readLine()) != null) {
		    	System.err.println(line);
		    }
		} finally {
		    if (stdInput != null) {
		        stdInput.close();
		    }
		    if(p!=null){
		        p.getInputStream().close();
		        p.getOutputStream().close();
		        p.getErrorStream().close();
		        p.destroy();
		    }
		}
	}

    public String Test(String featuresfile, String modelFile) {
        int folderposition=modelFile.lastIndexOf('/');
        String outputfile = featuresfile + "-annotatedWith-SVMmodel-" + modelFile.substring(folderposition+1, modelFile.lastIndexOf('.'));
        try 
        {
        	modelFile = getTemplate(modelFile);
          
        	MachineLearningHelper.CreateInputFormat(program_path, featuresfile);
        	ExecuteSVM(modelFile);
        	MachineLearningHelper.CreateOutput(program_path, outputfile);
        	MachineLearningHelper.ClearTmp(program_path);

        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (SVM):\n\t", e);
            return null;
        }
        return outputfile;
    }
    
	@Override
	public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang,
			ISentenceFormatter fromFormatter, ISentenceFormatter toFormatter)
	{
		String modelFile =  models_path + File.separator + "SVM" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition=modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
    	String outputfile = featuresfilePath + "-annotatedWith-SVMmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));

       // TokenizedFile file = new TokenizedFile(returnType, Languages.valueOf(lang.toUpperCase()), tokenizedFile.getName());
        try 
        {
        	//modelFile = getTemplate(modelFile);
           
			MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteSVM(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);
          
            UpdateTokenizedFile(formatter, toFormatter, outputfile);


        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (SVM):\n\t", e);
            return;
        }
	}
	
	@Override
	public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang,
			IFileFormatter fromFormatter, IFileFormatter toFormatter)
	{
		String modelFile =  models_path + File.separator + "SVM" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
		//String modelFile =  models_path + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition=modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
    	String outputfile = featuresfilePath + "-annotatedWith-SVMmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));

       // TokenizedFile file = new TokenizedFile(returnType, Languages.valueOf(lang.toUpperCase()), tokenizedFile.getName());
        try 
        {
        	//modelFile = getTemplate(modelFile);
           
			MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteSVM(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);
          
            UpdateTokenizedFile(formatter, toFormatter, outputfile);


        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (SVM):\n\t", e);
            return;
        }
	}

	 private void UpdateTokenizedFile(TokenFileFormatter formatter, ISentenceFormatter format, String outputfile)
				throws FileNotFoundException, IOException 
	    {
	    	BufferedReader fileReader = new BufferedReader(new FileReader(outputfile));
	    	try {
	    		String line="";
	    		while ((line = fileReader.readLine()) != null)
	    		{
	    			formatter.updateFromPipes(line, format);
	    		}
	    	} finally 
	    	{
	    		if (fileReader != null) 
	    		{
	    			fileReader.close();
	    		}

	    	}
		}
}
