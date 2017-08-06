package com.NLPFramework.Domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;


public class TokenizedFileHashtable extends Hashtable<String,TokenizedFile> implements Serializable
{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1979353129807571724L;

	/*public TokenizedFileHashtable(String path)
	{
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		ArrayList<TokenizedFile> filesRecovered = null;
		try
		{
			try
			{
				fin = new FileInputStream(path);
				ois = new ObjectInputStream(fin);
				filesRecovered = (ArrayList<TokenizedFile>) ois.readObject();
				for(TokenizedFile f : filesRecovered)
				{
					this.put(f.getName(), f);
				}

			}catch(Exception ex)
			{
				Logger.WriteError("Error reading binary file", ex);
			}finally
			{
				fin.close();
				ois.close();
			}
		}catch(Exception ex)
		{
			Logger.WriteError("Error reading binary file", ex);
		}
	}*/
	
	public TokenizedFileHashtable() {
		// TODO Auto-generated constructor stub
	}
	
	public Enumeration<String> getKeys()
	{
		return this.keys();
	}

	public File toFile(ISentenceFormatter formatter, String path)
	{
		File resultFile = new File(path);
		resultFile.delete();
		resultFile = new File(path);
		try {
			Enumeration<String> keys = this.keys();
			while(keys.hasMoreElements())
			{
				String fileName = keys.nextElement();
				TokenizedFile file = this.get(fileName);
				TokenFileFormatter fileFormatter = new TokenFileFormatter(file);
				File tempDir = FileHelper.GetFileAndCreateDir(resultFile.getAbsolutePath() + File.pathSeparator + "tmp");
				File temp = fileFormatter.toFile(tempDir.getAbsolutePath(), formatter);

				FileUtils.copyFileUtilappend(temp, resultFile);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultFile;
	}
	
	public File toFile(IFileFormatter formatter, String path)
	{
		File resultFile = new File(path);
		resultFile.delete();
		resultFile = new File(path);
		try {
			Enumeration<String> keys = this.keys();
			while(keys.hasMoreElements())
			{
				String fileName = keys.nextElement();
				TokenizedFile file = this.get(fileName);
				TokenFileFormatter fileFormatter = new TokenFileFormatter(file);
				File tempDir = FileHelper.GetFileAndCreateDir(resultFile.getAbsolutePath() + File.pathSeparator + "tmp");
				File temp = fileFormatter.toFile(tempDir.getAbsolutePath(), formatter);

				FileUtils.copyFileUtilappend(temp, resultFile);

			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultFile;
	}

	

}
