package com.NLPFramework.externalTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Formatters.SennaFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Helpers.FileHelper;


public class Senna extends BashProcessBase implements ITextProcessor {

    private static String program_path = "/home/pawett/tipSem/senna/";
    private static String program_bin = program_path + "senna-linux64";
   

 
   /* public static TokenizedFile run(String originalFilename, String lang, int tokenize, TokenizedFile file) {
        String outputfile = originalFilename + ".roth";
        try {
           
            String[] command = {"/bin/sh","-c","cat \""+originalFilename+"\" | "+program_bin + " -path " + program_path};
            run(command);
            //String[] command = {"/bin/sh","-c","cat \""+filename+"\" | "+program_bin+" "+String.valueOf(tokenize)+" 1 | "+program_bin2+" -f rothcomplete | sed \"s/^[[:blank:]]*\\$/|/\""};
            Process p = Runtime.getRuntime().exec(command);
            
            BufferedWriter output = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                String line;
                TokenFileFormatter formatter = new TokenFileFormatter(file);
                formatter.updateFromFileReader(stdInput, new SennaFormatter());
                              
            } finally {
                if (stdInput != null) {
                    stdInput.close();
                }
                if (output != null) {
                    output.close();
                }
                if(p!=null){
                    p.getInputStream().close();
                    p.getOutputStream().close();
                    p.getErrorStream().close();
                    p.destroy();
                }
            }

        } catch (Exception e) 
        {
           Logger.WriteError("Errors found (SRL_Roth):", e);
           
            return null;
        }
        return file;

    }
*/


	@Override
	public String runFromFile(String filePath) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public String runFromText(String text) {
		StringBuilder result = new StringBuilder();
		try {
			FileHelper.formatText(text);
			String[] command = {"/bin/sh","-c","echo \""+text+"\" | "+program_bin + " -path " + program_path + " -iobtags -usrtokens"};// -posvbs
			result.append(run(command));
			
		} catch (Exception e) {
			Logger.WriteError("Errors found (Senna):\n\t" , e);

			System.exit(1);

			return null;
		}
		return result.toString();
	}


	public String runNERFromSentence(String sentence)
	{
		StringBuilder result = new StringBuilder();
		try {
			
			String[] command = {"/bin/sh","-c","echo \""+sentence+"\" | "+program_bin + " -path " + program_path + " -ner -usrtokens -iobtags"};// -posvbs
	
			result.append(run(command));
			Process p = Runtime.getRuntime().exec(command);

		} catch (Exception e) {
			Logger.WriteError("Errors found (Senna):\n\t" , e);

			System.exit(1);

			return null;
		}
		return result.toString();
		
	}
	
	public String runSRLFromSentence(String sentence)
	{
		StringBuilder result = new StringBuilder();
		try 
		{	
			String[] command = {"/bin/sh","-c","echo \""+sentence+"\" | "+program_bin + " -path " + program_path + " -srl -usrtokens -iobtags"};// -posvbs
	
			result.append(run(command));

		} catch (Exception e) {
			Logger.WriteError("Errors found (Senna):\n\t" , e);

			System.exit(1);
			return null;
		}
		return result.toString();
		
	}

	@Override
	public void runFromSentence(TokenizedSentence sentence) 
	{
		// TODO Auto-generated method stub
		
	}
}
