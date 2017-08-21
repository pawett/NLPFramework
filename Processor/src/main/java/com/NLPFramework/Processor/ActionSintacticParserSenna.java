package com.NLPFramework.Processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.Senna;

public class ActionSintacticParserSenna extends ActionSintacticParserBase {

	@Override
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception {
		Senna executor = new Senna();
		for(TokenizedSentence sentence : tokFile)
		{
			if(sentence.isEmpty())continue;
		
			String processedText = executor.runSRLFromSentence(sentence.toString());
			InputStream is = new ByteArrayInputStream(processedText.getBytes());
			BufferedReader sentenceReader = new BufferedReader(new InputStreamReader(is));
			String line = "";
			
			Word currentWord = sentence.getFirst();
			
			try 
			{
				line = sentenceReader.readLine();
				while(line != null)
				{
					String[] valuesArray = line.split("\t");
					String word = valuesArray[0];
					String syntacticParse = valuesArray[1];
					
					if(currentWord.word.equals(word))
					{
						currentWord.syntacticTree = syntacticParse;
					}
					else
						Logger.WriteDebug(String.format("%s does not match with %s", currentWord.word, word));
					
					line = sentenceReader.readLine();
				}
				
			} catch (IOException e) 
			{
				// TODO Auto-generated catch block
				Logger.WriteError("Error updating from external tool", e);
			}	
		}
		return tokFile;
	}

}
