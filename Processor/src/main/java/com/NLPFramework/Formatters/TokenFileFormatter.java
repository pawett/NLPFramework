package com.NLPFramework.Formatters;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.ITextProcessor;


public class TokenFileFormatter {
	
	private TokenizedFile file = null;
	//private IWordFormatter format = null;
	//private ISentenceFormatter sentenceFormatter = null;
	//private IFileFormatter fileFormatter = null;
	
	public TokenFileFormatter(TokenizedFile file)
	{
		this.file = file;
	}
	
	/*public TokenFileFormatter(TokenizedFile file, IWordFormatter format)
	{
		this.file = file;
		this.format = format;
	}
	
	public TokenFileFormatter(TokenizedFile file, ISentenceFormatter format)
	{
		this.file = file;
		this.sentenceFormatter = format;
	}
	
	public TokenFileFormatter(TokenizedFile file, IFileFormatter format)
	{
		this.file = file;
		this.fileFormatter = format;
	}
	
	public void setFormat(IWordFormatter format)
	{
		this.format = format;
	}
	
	public void setFormat(ISentenceFormatter format)
	{
		this.sentenceFormatter = format;
	}*/
	
	public void addFromPipes(String line, IWordFormatter format)
	{		
		 String[] linearr = line.split("\t");
		 if(file.isEmpty())
		 {
			 TokenizedSentence s = new TokenizedSentence();
			 file.add(s);	
		 }
		
		int numSentence = file.size()-1;
		int numWord = file.getLast().size();
		Word w = new Word();
		w.file = file.getName();
		w.sentenceNumber = numSentence;
		w.sentencePosition = numWord;
		format.setValues(w, line);
		file.getLast().add(w);
		
		if(format.isSentenceEnd(line))
		{
			TokenizedSentence s = new TokenizedSentence();
			file.add(s);
		}
	}
	
	public void updateFromPipes(String line, IFileFormatter formatter)
	{
		formatter.setValues(line, (TimeMLFile)file);	
	}
	
	public void updateFromPipes(String line, ISentenceFormatter formatter)
	{
		int sentenceId = getSentenceId(line);
					
		TokenizedSentence s = file.get(sentenceId);
		Word firstElement = null;
		formatter.setValues(s, line, firstElement);
		
	}
	
	public void updateFromPipes(String line, IWordFormatter formatter)
	{
		String[] lineArray = line.split("\\|");
		int sentenceId = getSentenceId(line);
		
		String wordP = lineArray[2];
		int wordPosition = Integer.parseInt(wordP);
		
		TokenizedSentence s = file.get(sentenceId);
		Word word = s.get(wordPosition - 1);
		formatter.setValues(word, line);
		
	}
	
	public void updateFromExternalTool(ITextProcessor processor)
	{
		for(TokenizedSentence sentence : file)
		{
			processor.runFromSentence(sentence);
		}
	}
	
	public void updateFromExternalTool(ITextProcessor processor, IWordFormatter format)
	{
		for(TokenizedSentence sentence : file)
		{		
			String processedText = processor.runFromText(sentence.toString());
			InputStream is = new ByteArrayInputStream(processedText.getBytes());
			BufferedReader sentenceReader = new BufferedReader(new InputStreamReader(is));
			String line = "";
			if(sentence.isEmpty())continue;
			Word currentWord = sentence.getFirst();
			boolean isCorrect = true;
			while(currentWord != null)
			{
				try 
				{
					
					/*if(isCorrect)
					{
						line = sentenceReader.readLine();
						if (line == null )return;
						if(line.isEmpty()) continue;
					}*/
					do
					{
						line = sentenceReader.readLine();
						isCorrect = format.setValues(currentWord, line);
						if(!isCorrect)
						{
							Logger.WriteDebug("Error in Senna: " +currentWord + " :: " + line);
							isCorrect = format.setValues(currentWord.next, line);
							if(isCorrect)
								currentWord = currentWord.next;
							
						}
							
					}while(line != null && !isCorrect);
					
					
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					Logger.WriteError("Error updating from external tool", e);
				}	
				currentWord = currentWord.next;
				
			}
			/*for (Word word : sentence)
			{
				try {
					line = sentenceReader.readLine();
					if (line == null )return;
					if(line.isEmpty()) continue;
					format.setValues(word, line);
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					Logger.WriteError("Error updating from external tool", e);
				}				
				
			}*/
		}
	}
	
	
	public void updateFromExternalToolBySentence(ITextProcessor processor, ISentenceFormatter format)
	{
		for(TokenizedSentence sentence : file)
		{		
			String processedText = processor.runFromText(sentence.toString());
			InputStream is = new ByteArrayInputStream(processedText.getBytes());
			BufferedReader sentenceReader = new BufferedReader(new InputStreamReader(is));
			String line = "";
			if(sentence.isEmpty())continue;
			Word currentWord = sentence.getFirst();
			Word firstElement = null;
			boolean isCorrect = true;
			while(currentWord != null)
			{
				try {
					if(isCorrect)
					{
						line = sentenceReader.readLine();
						if (line == null )return;
						if(line.isEmpty()) continue;
					}
					format.setValues(sentence, processedText, firstElement);
				} catch (IOException e) 
				{
					// TODO Auto-generated catch block
					Logger.WriteError("Error updating from external tool", e);
				}	
				currentWord = currentWord.next;	
			}
		}
	}
	
	public void updateFromFileReader(BufferedReader fileReader, IWordFormatter format) throws IOException
	{
		try {
			String line="";

			for(TokenizedSentence sentence : file)
			{
				for (Word word : sentence)
				{
					line = fileReader.readLine();
					if (line == null )return;
					if(line.isEmpty()) continue;
					format.setValues(word, line);
				}
			} 	        	

		} finally 
		{
			if (fileReader != null) 
			{
				fileReader.close();
			}

		}
	}
	
	public String toString(IFileFormatter formatter)
	{
		return formatter.toString((TimeMLFile)file);
		/*StringBuilder sb = new StringBuilder();
		int numSentence = 0;
		for(TokenizedSentence sentence : file)
		{
	
			sb.append(sentenceToString(sentence, numSentence));
			numSentence++;
		}		
		return sb.toString();*/
	}
	
	public String toString(ISentenceFormatter formatter)
	{
		StringBuilder sb = new StringBuilder();
		
		for(TokenizedSentence sentence : file)
		{
			sb.append(formatter.toString(sentence));
			sb.append(System.lineSeparator());
		}		
		return sb.toString();
	}
	
	public String toString(IWordFormatter formatter)
	{
		StringBuilder sb = new StringBuilder();
		
		for(TokenizedSentence sentence : file)
		{
			for(Word w : sentence)
			{
				sb.append(formatter.toString(w));
				sb.append(System.lineSeparator());
			}

			sb.append(System.lineSeparator());
		}		
		return sb.toString();
	}
	
	/*public String sentenceToString(TokenizedSentence sentence, int numSentence)
	{
		StringBuilder sb = new StringBuilder();
		int numWord = 0;
		for(Word word : sentence)
		{
			//word.sent_num = String.valueOf(numSentence);
			String line = format.toString(word);
			if(line != null && !line.equals(""))
			{
				sb.append(line);
				sb.append(System.lineSeparator());
			}
			numWord++;
		}
		//sb.append(System.getProperty("line.separator"));
		return sb.toString();
	}*/
	
	/*public File toFile()
	{
		return toFile(null);
	}*/
	
	public File toFile(String path, IFileFormatter formatter)
	{
		String fileName = file.getName() + "." + formatter.getExtension();
		if(path != null)
			fileName = path + File.separator + fileName;
		
		File f = new File(fileName);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

			try{
				output.write(this.toString(formatter));
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + fileName, e);
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (IOException e1) {
			Logger.WriteError("Cannot create the file in the path" + fileName, e1);
		}
		return f;
		
	}
	
	public File toFile(String path, ISentenceFormatter formatter)
	{
		String fileName = file.getName() + "." + formatter.getExtension();
		if(path != null)
			fileName = path + File.separator + fileName;
		
		File f = new File(fileName);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

			try{
				output.write(this.toString(formatter));
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + fileName, e);
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (IOException e1) {
			Logger.WriteError("Cannot create the file in the path" + fileName, e1);
		}
		return f;
		
	}
	
	public File toFile(String path, IWordFormatter formatter)
	{
		String fileName = file.getName() + "." + formatter.getExtension();
		if(path != null)
			fileName = path + File.separator + fileName;
		
		File f = new File(fileName);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

			try{
				output.write(this.toString(formatter));
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + fileName, e);
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (IOException e1) {
			Logger.WriteError("Cannot create the file in the path" + fileName, e1);
		}
		return f;
		
	}
	
	

	private int getSentenceId(String line)
	{
		String[] lineArray = line.split("\\|");
		String sentenceNumber = lineArray[1];
		/*if(sentenceNumber.startsWith("l") || file.getType().equals(FilesType.TempEval2_features_annotatedWith))//for TLinks cat
		{
			sentenceNumber = sentenceNumber.replaceAll("l", "");
			return (Integer.parseInt(sentenceNumber)-1);
			
		}*/
		return Integer.parseInt(sentenceNumber);
	
	}

}
