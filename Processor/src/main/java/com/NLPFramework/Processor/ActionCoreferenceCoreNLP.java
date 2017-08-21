package com.NLPFramework.Processor;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.Entity;
import com.NLPFramework.Domain.JournalistInfo;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.externalTools.StanfordCoreSingleton;

import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefChainAnnotation;
import edu.stanford.nlp.coref.CorefCoreAnnotations.CorefMentionsAnnotation;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.coref.data.CorefChain.CorefMention;
import edu.stanford.nlp.coref.data.Mention;
import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.IndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
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
				if(mainCoreference  == null)
					mainCoreference = subCoreference;
				else
					mainCoreference.addCoref(subCoreference);
			}
			if(mainCoreference != null)
				tokFile.addAnnotation(Coreference.class, mainCoreference);
		}
		
		
	/*	for(CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class) )
		{
			Collection<RelationTriple> triples = sentence.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			for(RelationTriple triple : triples)
			{
				Logger.Write(String.valueOf(triple.confidence));
				Logger.Write(triple.subjectLemmaGloss());
				Logger.Write(triple.relationLemmaGloss());
				Logger.Write(triple.objectLemmaGloss());
			}
		}*/
		
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{		
			List<Mention> mentions = s.get(CorefMentionsAnnotation.class);
			for(Mention m : mentions)
			{
				TokenizedSentence sentence = tokFile.get(m.sentNum);
				CorefChain chain = coreferences.get(m.corefClusterID);
				Logger.Write(m.lowercaseNormalizedSpanString());
				if(chain != null)
				Logger.Write(chain.toString());
				if(m.isSubject || m.isDirectObject)
				{
					CoreLabel iw = m.dependingVerb.backingLabel();
					int depVerbPosition = iw.get(IndexAnnotation.class)-1;
					
					Entity e = new Entity();
					e.word = sentence.get(m.startIndex);
					e.offset = m.endIndex - m.startIndex;
					
					Word depVerb = sentence.get(depVerbPosition);//.getWordDependantVerb(e.word);
					EntityMapper<com.NLPFramework.Domain.Annotation> map = sentence.annotations.get(JournalistInfo.class) != null ? sentence.annotations.get(JournalistInfo.class).get(depVerb) : null;
					if(map == null)
						 map = new EntityMapper<>();
					JournalistInfo ji = map.element != null ? (JournalistInfo) map.element : new JournalistInfo();
					
					if(m.isDirectObject)
					{
						e.role = PropBankArgument.A1;
						ji.patients.add(e);
					}
					else
					{
						e.role = PropBankArgument.A0;
						ji.actors.add(e);
					}
					
					if(sentence.annotations.get(Event.class) != null && sentence.annotations.get(Event.class).get(depVerb) != null)
						ji.what = (Event)sentence.annotations.get(Event.class).get(depVerb).element;
					else
						continue;
					map.element = ji;
					if(sentence.annotations.get(JournalistInfo.class) == null || sentence.annotations.get(JournalistInfo.class).get(depVerb) == null)
						sentence.addAnnotation(JournalistInfo.class, ji.what.word, map);
				}
					
			}
		}

		return tokFile;
	}

	private Coreference setCoreference(TokenizedFile tokFile, CorefMention corefMention) 
	{
		TokenizedSentence sentence = tokFile.get(corefMention.sentNum - 1);
		int corefIndex = corefMention.startIndex - 1;
		if(corefIndex < 0)
			return null;
		Word word = sentence.get(corefIndex);
		int offset = corefMention.endIndex - corefMention.startIndex;
		return new Coreference(word, offset);
		
	}

}
