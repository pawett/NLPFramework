package com.NLPFramework.externalTools;

import java.io.*;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Helpers.FileUtils;


/**
 *
 * @author Hector Llorens
 * @since 2011
 */
public class TempEval_scorer {
    private static String program_path = FileUtils.getApplicationPath() + "program-data/TempEval_scorer/";

    public static String score_entities(String extents, String attribs, String lang, String element, String keyspath) {
        String outputfile = extents.substring(0,extents.lastIndexOf("/")+1) + "TempEval_score.txt";
        try {
            // CREATE APROPRIATE INPUT FORMAT
            String[] command = {"/bin/sh","-c","python \""+program_path+"score_entities.py\" "+keyspath+lang+"/test/entities/base-segmentation.tab "+keyspath+lang+"/test/entities/"+element+"-extents.tab "+extents+" "+keyspath+lang+"/test/entities/"+element+"-attributes.tab "+keyspath+lang+"/test/entities/"+element+"-attributes.tab > "+outputfile};
            Process p=Runtime.getRuntime().exec(command);
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
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
        } catch (Exception e) 
        {
            Logger.WriteError("Errors found (TempEval Scorer):\n\t", e);
            return null;
        }
        return outputfile;
    }



    public static void printscore_entities(String file, String lang, String element) {
        try {
            // CREATE APROPRIATE INPUT FORMAT
            String[] command = {"/bin/sh","-c","python \""+program_path+"score_entities.py\" "+file};
            Process p=Runtime.getRuntime().exec(command);
            
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            try {
                String line;
                while ((line = stdInput.readLine()) != null) {
                    System.out.println(line);
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
        } catch (Exception e)
        {
           Logger.WriteError("Errors found (TempEval Scorer):\n\t", e);
        }
    }














}
