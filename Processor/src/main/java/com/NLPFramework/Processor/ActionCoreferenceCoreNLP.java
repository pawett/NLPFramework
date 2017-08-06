package com.NLPFramework.Processor;

import java.util.List;
import java.util.Map;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.StanfordCoreSingleton;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefMentionsAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ActionCoreferenceCoreNLP extends ActionTokenizerBase {

	@Override
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception
	{
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getCoreferencePipeLine();		
		
		Annotation doc = new Annotation( tokFile.getOriginalText());
				
		pipeline.annotate(doc);
		
		Map<Integer,CorefChain> coreferences = doc.get(CorefChainAnnotation.class);
		for(CorefChain corefs : coreferences.values())
		{
			CorefMention representative = corefs.getRepresentativeMention();
			Coreference mainCoreference = setCoreference(tokFile, representative);
			
			for(CorefMention subordinateMention : corefs.getMentionsInTextualOrder())
			{
				Coreference subCoreference = setCoreference(tokFile, subordinateMention);
				mainCoreference.addCoref(subCoreference);
			}
			tokFile.addAnnotation(Coreference.class, mainCoreference);
		}
		/*for(CoreMap s : doc.get(SentencesAnnotation.class))
		{		
			List<Mention> mentions = s.get(CorefMentionsAnnotation.class);
			for(Mention m : mentions)
			{
				CorefChain chain = coreferences.get(m.corefClusterID);
				Logger.Write(m.lowercaseNormalizedSpanString());
			}
		}*/

		return tokFile;
	}

	private Coreference setCoreference(TokenizedFile tokFile, CorefMention corefMention) 
	{
		TokenizedSentence sentence = tokFile.get(corefMention.sentNum - 1);
		int corefIndex = corefMention.startIndex - 2;
		if(corefIndex < 0)
			return null;
		Word word = sentence.get(corefIndex);
		int offset = corefMention.endIndex - corefMention.startIndex;
		return new Coreference(word, offset);
		
	}

}
