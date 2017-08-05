package com.NLPFramework.Processor;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.StanfordCoreSingleton;

import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ActionLemmatizerCoreNLP extends ActionLemmatizerBase {

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
					Word w = sentence.get(token.index());
					String coreNLPWord = token.get(TextAnnotation.class); 
					if(w.word.equals(coreNLPWord))
						w.lemma = token.get(LemmaAnnotation.class);
					else
						Logger.WriteDebug(String.format("%s does not match with %s", w.word, coreNLPWord));
				}
			}
			
		}
		return tokFile;
	}

}
