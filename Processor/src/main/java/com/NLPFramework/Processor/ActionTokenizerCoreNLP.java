package com.NLPFramework.Processor;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.CoreNLPSentenceSplitter;
import com.NLPFramework.externalTools.StanfordCoreSingleton;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ActionTokenizerCoreNLP extends ActionTokenizerBase {

	@Override
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception
	{
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();		
		
		for(TokenizedSentence sentence : tokFile)
		{
			Annotation doc = new Annotation(sentence.getOriginalText());
			
			pipeline.annotate(doc);
			for(CoreMap s : doc.get(SentencesAnnotation.class))
			{		  
				TokenizedSentence tokSent = new TokenizedSentence();
				
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
