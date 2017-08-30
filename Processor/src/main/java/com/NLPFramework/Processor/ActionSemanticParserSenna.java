package com.NLPFramework.Processor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.SennaFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.externalTools.Senna;

public class ActionSemanticParserSenna extends ActionSemanticParserBase {

	@Override
	public TokenizedFile execute(TokenizedFile tokFile)
	{
		Senna executor = new Senna();
		TokenFileFormatter formatter = new TokenFileFormatter(tokFile);
		formatter.updateFromExternalTool(executor, new SennaFormatter());
		for(TokenizedSentence sentence : tokFile)
		{
			sentence.verbs = new ArrayList<>(sentence.stream().filter(w -> w.isVerb).collect(Collectors.toList()));
		
			int verbPos = 0;
			for(Word verb : sentence.verbs)
			{
				for(Word w : sentence)
				{
					if(w.semanticRoles != null && w.semanticRoles.size() > 0)
					{
						SemanticRole sr = w.semanticRoles.get(verbPos);
						sentence.addSemanticRole(sr.argument, verb, w);
					}
					
				}
				verbPos++;
			}
			
		}
		return tokFile;
		
		
		/*for(TokenizedSentence sentence : tokFile)
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
					
					if(currentWord.word.equals(word))
					{
						if(!valuesArray[1].trim().equals("-") && currentWord.pos.startsWith("V"))
							currentWord.isVerb = true;
						for(int i = 2; i <= valuesArray.length -1; i++)
							currentWord.setRoleFromText(valuesArray[i].trim());
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
		return tokFile;*/
	}

}
