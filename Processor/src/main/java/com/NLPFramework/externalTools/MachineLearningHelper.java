package com.NLPFramework.externalTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MachineLearningHelper {

	public static void ClearTmp(String program_path) throws IOException {
		Process p;
		BufferedReader stdInput;
		String[] command4 = {"/bin/sh", "-c", "rm -rf " + program_path + "*.tmp"};
		p = Runtime.getRuntime().exec(command4);
		
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

	public static void CreateOutput(String program_path, String outputfile) throws IOException {
		Process p;
		BufferedReader stdInput;
		String[] command3 = {"/bin/sh", "-c", "tr \"\t\" \"|\" < " + program_path + "temp2.tmp" + " | sed '/^[[:blank:]]*$/d' >" + outputfile};
		p = Runtime.getRuntime().exec(command3);
		
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

	public static void CreateInputFormat(String program_path, String featuresfilePath) throws IOException 
	{
		String[] command = {"/bin/sh", "-c", "tr \"|\" \" \" < " + featuresfilePath + " | sed \"s/^[[:blank:]]*\\$//\" > " + program_path + "temp.tmp"};
		Process p = Runtime.getRuntime().exec(command);
		
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
	}

}
