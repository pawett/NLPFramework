package com.NLPFramework.Helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;


public class FileHelper {

	public static ArrayList<File> getFilesFromPath(ArrayList<String> filesPath) throws FileNotFoundException
	{
		ArrayList<File> files = new ArrayList<File>();
	   	if (filesPath != null) {
	   	    //System.err.println("File/s to annotate: " + input_files.length);

	   	    for (String filePath : filesPath) 
	   	    {
	   	        // Check files - exist/encoding
	   	    	File file = new File(filePath);
	   	    	if (!file.exists()) 
	   	    	{
	   	    		throw new FileNotFoundException("File does not exist neither locally nor in Wikipedia: " + file);
	   	    	}

	   	        if (!file.isFile()) {
	   	            throw new IllegalArgumentException("Must be a regular file: " + file);
	   	        }

	   	        files.add(file);
	   	       }
	   	}
	   	return files;
	}
	
	public static File GetFileAndCreateDir(String path) throws Exception
	{
		 File dir = new File(path);
         if (!dir.exists()) {
             if (!dir.mkdirs()) {  // mkdir only creates one, mkdirs creates many parent dirs if needed
                 throw new Exception("Directory not created...");
             }
         }
         return dir;
	}
	
	public static String formatText(String text)
	{
		//text = text.replaceAll("\"", "''");
		text = text.replaceAll("``", "''");
		text = text.replaceAll("`", "'");
		text = text.replaceAll("-LSB-", "(");
		text = text.replaceAll("-RSB-", ")");
		text = text.replaceAll("-LCB-", "(");
		text = text.replaceAll("-RCB-", ")");
		text = text.replaceAll("-LRB-", "(");
		text = text.replaceAll("-RRB-", ")");
		//text = text.replaceAll("-", "-");
		return text;
		//.replace("''", "\"").replace("``","\"").replace("-", " - ");
	}
	
	public static String AppendTabs(String text) 
	{
		return "\t"+text;
	}
	
	public static String AppendTabs(int text) 
	{
		return "\t"+text;
	}
	
	public static boolean createFileFromText(String text, String filePath)
	{
		BufferedWriter output;
		try {
			File plain = new File(filePath);
			output = new BufferedWriter(new FileWriter(plain.getAbsolutePath()));

			try{
				output.write(formatText(text));
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + plain.getAbsolutePath(), e);
				return false;
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (Exception e1) {
			Logger.WriteError("Cannot create the file in the path" + filePath, e1);
			return false;
		}
		return true;
	}
	
	public static String saveFilesAsBinary(TokenizedFileHashtable files, String path)
	{
		  FileOutputStream fos = null;
          ObjectOutputStream oos = null;
          String binaryPath = path + "features.obj";
          try{
        	  fos = new FileOutputStream(binaryPath);
        	  oos = new ObjectOutputStream(fos);

        	  oos.writeObject(files);
        	  return binaryPath;
          }catch (Exception ex)
          {
        	  Logger.WriteError("Error saving binary file", ex);
          }finally
          {
        	  try {
        		  oos.close();
        	  } catch (IOException e) {
        		  // TODO Auto-generated catch block
        		  e.printStackTrace();
        	  }
        	  try {
        		  fos.close();
        	  } catch (IOException e) {
        		  // TODO Auto-generated catch block
        		  e.printStackTrace();
        	  }
          }
          
          return null;
          
	}
	
	public static TokenizedFileHashtable getBinaryFiles(String path)
	{
		FileInputStream fin = null;
		ObjectInputStream ois = null;
		ArrayList<TokenizedFile> filesRecovered = null;
		TokenizedFileHashtable files = new TokenizedFileHashtable();
		try
		{
			try
			{
				fin = new FileInputStream(path);
				ois = new ObjectInputStream(fin);
				files = (TokenizedFileHashtable) ois.readObject();
				/*for(TokenizedFile f : filesRecovered)
				{
					files.put(f.getName(), f);
				}*/
				
				return files;

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
		
		return null;
	}
	
}
