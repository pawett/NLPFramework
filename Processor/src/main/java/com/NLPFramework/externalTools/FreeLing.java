package com.NLPFramework.externalTools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Formatters.FreeLingFormatter;
import com.NLPFramework.Formatters.ITokenizer;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Helpers.FileUtils;


/**
 * REGULAR INSTALLATION AND INCLUSION IN PATH REQUIRED
 * @author Hector Llorens
 * @since 2011
 */
public class FreeLing implements ITokenizer {

    // path for configurations, temporal files (if there are), or default templates
    public static String program_path = FileUtils.getApplicationPath() + "program-data/FreeLing/";


    public TokenizedFile run(String originalFilename, TokenizedFile file) {
        String outputfile = originalFilename + ".freeling";
       // program_path = "/usr/local/share/freeling/config/";
        String lang =file.getLanguage().toString();
        
        try {
        	        
          String[] command=new String[3];
            command[0]="/bin/sh";
            command[1]="-c";
            // Freeling is rule based and uses ISO internally so needs ISO as input...
          /*  if(tokenize==0){
                command[2]="cat \""+originalFilename+"\" | tr \"|\" \"-\" | iconv -c -t iso-8859-1 | analyze -f "+program_path+lang.toLowerCase()+".cfg --input token | iconv -c -f iso-8859-1 | cut -f 1-3 -d \" \" | tr \" \" \"|\" | sed \"s/^[[:blank:]]*\\$/|/\"";
            }else{*/
            	if(lang.equalsIgnoreCase("en"))
            	     command[2]="analyze -f "+program_path+lang.toLowerCase()+".cfg --input text <"+originalFilename;//+"| cut -f 1-3 -d \" \" | tr \" \" \"|\" | sed \"s/^[[:blank:]]*\\$/|/\"";
            	else
            		command[2]="cat \""+originalFilename+"\" | tr \"|\" \"-\" | iconv -c -t iso-8859-1 | analyze -f "+program_path+lang.toLowerCase()+".cfg --input text | iconv -c -f iso-8859-1 | cut -f 1-3 -d \" \" | tr \" \" \"|\" | sed \"s/^[[:blank:]]*\\$/|/\"";
            //}

            // UTF-8 works but tagging does not work properly
            /*if(tokenize==0){
                command[2]="cat \""+filename+"\" | tr \"|\" \"-\" | analyze -f "+program_path+lang.toLowerCase()+".cfg --inpf token | cut -f 1-3 -d \" \" | tr \" \" \"|\" | sed \"s/^[[:blank:]]*\\$/|/\"";
            }else{
                command[2]="cat \""+filename+"\" | tr \"|\" \"-\" | analyze -f "+program_path+lang.toLowerCase()+".cfg --inpf plain | cut -f 1-3 -d \" \" | tr \" \" \"|\" | sed \"s/^[[:blank:]]*\\$/|/\"";
            }*/

            //System.out.println(command[2]);

            Process p = Runtime.getRuntime().exec(command);
            
            BufferedWriter output = new BufferedWriter(new FileWriter(outputfile));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
            	TokenFileFormatter formatter = new TokenFileFormatter(file);
            	formatter.updateFromFileReader(stdInput, new FreeLingFormatter());
            	
               /* String line;
                TokenizedSentence sentence = new TokenizedSentence();
                while ((line = stdInput.readLine()) != null) 
                {
                	file.getFormatter().updateFromPipes(line);
                }*/
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
            
            return file;

        } catch (Exception e) {
        	Logger.WriteError("Errors found (SRL_Roth):\n\t", e);
        	System.exit(1);
        	return null;
        }
    }
}


