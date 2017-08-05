package com.NLPFramework.externalTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLPSentenceSplitter {

	public static TokenizedFile run(TokenizedFile tokFile)
	{
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();
		Annotation doc = new Annotation(tokFile.getOriginalText());
		
		pipeline.annotate(doc);
			
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{			
			TokenizedSentence tokSent = new TokenizedSentence();
			tokSent.originalText = s.get(TextAnnotation.class);
			tokFile.add(tokSent);
		}
		return tokFile;
	}
	
	public static TokenizedFile runTokenizer(TokenizedFile tokFile)
	{
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();		
		
		for(TokenizedSentence sentence : tokFile)
		{
			Annotation doc = new Annotation(sentence.getOriginalText());
			
			pipeline.annotate(doc);
			for(CoreMap s : doc.get(SentencesAnnotation.class))
			{		  
				TokenizedSentence tokSent = new TokenizedSentence();
						
				boolean isInsideTime = false;
				int tokNum = 0;
				 for (CoreLabel token: s.get(TokensAnnotation.class))
				{
					Word w = new Word();
					w.word = token.get(TextAnnotation.class);
					sentence.add(w);
				}
			}
			
		}
		return tokFile;
	}
}
