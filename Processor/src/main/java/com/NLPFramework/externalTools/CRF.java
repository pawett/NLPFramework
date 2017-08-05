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
public class CRF implements IMachineLearningMethod
{
    // path is not necessary but is used to capture temporal files (if there are), or default templates
    public static String program_path = "/home/pawett/NLPFramework/Processor/program-data/CRF/";
    		//FileUtils.getApplicationPath() + "program-data/CRF++/";
    private final String modelFileExtension = ".CRFmodel";
   
    
    /**
     * Runs CRF++ over a features file given a model
     * and saves the output as input-annotatedWith-CRFmodel-x file
     *
     * The model must be in the same path or in program-data/CRF++
     *
     * Format | | | | pipes
     *
     * @param filename
     * @param template
     * @return Output filename
     */
    public String test(String featuresfile, String modelFile) {
        int folderposition = modelFile.lastIndexOf('/');
        String outputfile = featuresfile + "-annotatedWith-CRFmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));
        try 
        {
        	modelFile = getTemplate(modelFile);
            
            MachineLearningHelper.CreateInputFormat(program_path, featuresfile);
            ExecuteCRF(outputfile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);


        } catch (Exception e) 
        {
        	Logger.WriteError("Errors found (CRF++):", e);
            
            return null;
        }
        return outputfile;
    }
    
  
    public String Train(String featuresfile, String templatefile)
    {
    	String outputfile = featuresfile + "." + templatefile.substring(0, templatefile.lastIndexOf('.')) + ".CRFmodel";
    	try 
    	{
    		templatefile = getTemplate(templatefile);

    		executeCRFLearn(featuresfile, templatefile, outputfile);

    		MachineLearningHelper.ClearTmp(program_path);

    	} catch (Exception e)
    	{
    		Logger.WriteError("Errors found (CRF++):\n\t", e);

    		return null;
    	}
    	return outputfile;

    }


	private void executeCRFLearn(String featuresfile, String templatefile, String outputfile) throws IOException 
	{
		Process p;
		BufferedReader stdInput;
		MachineLearningHelper.CreateInputFormat(program_path, featuresfile);


		String[] command2 = {"crf_learn", "-c", "1.0", "-p", "4", templatefile, program_path + "temp.tmp", outputfile};
		//System.err.println("\ncrf_learn -c 1.0 -p 2 " + templatefile + " " + featuresfile+" "+outputfile+"\n");
		p = Runtime.getRuntime().exec(command2);
		
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

    /**
     * Runs CRF++ over a features file given a model
     * and saves the output as input-annotatedWith-CRFmodel-x file
     *
     * The model must be in the same path or in program-data/CRF++
     *
     * Format | | | | pipes
     *
     * @param filename
     * @param template
     * @return Output filename
     */
    public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, IWordFormatter fromFormatter, IWordFormatter toFormatter) 
    {
    	String modelFile =  models_path + File.separator + "CRF" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
    	//String modelFile =  models_path + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition = modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
        String outputfile = featuresfilePath + "-annotatedWith-CRFmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));
       
        try 
        {
        	modelFile = getTemplate(modelFile);
           
        	MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteCRF(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);
            UpdateTokenizedFile(formatter, toFormatter, outputfile);

        } catch (Exception e)
        {
            Logger.WriteError("Errors found (CRF++):\n\t", e);
            return ;
        }
      
    }
    
    
    public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, ISentenceFormatter fromFormatter, ISentenceFormatter toFormatter)
    {
    	String modelFile =  models_path + File.separator + "CRF" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition = modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
    	String outputfile = featuresfilePath + "-annotatedWith-CRFmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));

        try 
        {
        	modelFile = getTemplate(modelFile);

        	MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteCRF(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);

            UpdateTokenizedFile(formatter, toFormatter, outputfile);

        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (CRF++):\n\t", e);
            return ;
        }
    }
    
    public void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, IFileFormatter fromFormatter, IFileFormatter toFormatter)
    {
    	String modelFile =  models_path + File.separator + "CRF" + File.separator + "models" + File.separator + approach + "_" + type + "_" + lang;
    	modelFile =	modelFile + modelFileExtension;
    	int folderposition = modelFile.lastIndexOf('/');
    	TokenFileFormatter formatter = new TokenFileFormatter(tokenizedFile);
    	String featuresfilePath = formatter.toFile(null, fromFormatter).getAbsolutePath();
    	String outputfile = featuresfilePath + "-annotatedWith-CRFmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));

        try 
        {
        	modelFile = getTemplate(modelFile);

        	MachineLearningHelper.CreateInputFormat(program_path, featuresfilePath);
            ExecuteCRF(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);

            UpdateTokenizedFile(formatter, toFormatter, outputfile);

        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (CRF++):\n\t", e);
            return ;
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
    
    private void UpdateTokenizedFile(TokenFileFormatter formatter, IFileFormatter format, String outputfile)
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


    public String Test(String featuresfile, String modelFile) 
    {
        int folderposition = modelFile.lastIndexOf('/');
        String outputfile = featuresfile + "-annotatedWith-CRFmodel-" + modelFile.substring(folderposition + 1, modelFile.lastIndexOf('.'));
        try {
        	modelFile = getTemplate(modelFile);
        	MachineLearningHelper.CreateInputFormat(program_path, featuresfile);
            ExecuteCRF(modelFile);
            MachineLearningHelper.CreateOutput(program_path, outputfile);
            MachineLearningHelper.ClearTmp(program_path);
       
        } catch (Exception e)
        {
           Logger.WriteError("Errors found (CRF++):",  e);

            return null;
        }
        return outputfile;
    }

	private void ExecuteCRF(String modelfile) throws IOException 
	{
		Process p;
		BufferedReader stdInput;
		String[] command2 = {"crf_test", "-m", modelfile, program_path + "temp.tmp", "-o", program_path + "temp2.tmp"};
		p = Runtime.getRuntime().exec(command2);
		
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


	


}
