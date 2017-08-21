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

public class BashProcessBase 
{

	protected String formatLine(String text)
	{
		return text;
	}
	protected String run(String[] command) 
	{ 
		StringBuilder result = new StringBuilder();
		try
		{        	
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try {
				String line;
				while ((line = stdInput.readLine()) != null)
				{
					line = formatLine(line);
					result.append(line);
					result.append(System.lineSeparator());
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
			Logger.WriteError("Errors found executing command:", e);

			return null;
		}
		return result.toString();

	}
	
	protected String runOnlyLogging(String[] command) 
	{ 
		StringBuilder result = new StringBuilder();
		try
		{        	
			Process p = Runtime.getRuntime().exec(command);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			try {
				String line;
				while ((line = stdInput.readLine()) != null)
				{
					line = formatLine(line);
					Logger.WriteDebug(line);
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
			Logger.WriteError("Errors found executing command:", e);

			return null;
		}
		return result.toString();

	}

}
