package com.NLPFramework.Helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.EntityType;
import com.NLPFramework.Domain.NER;
import com.NLPFramework.Domain.NERCoreference;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.TimeMLFile;

public class NERCoreferenceHelper
{
	public static void addCoreference(TimeMLFile file, EntityMapper<NER> map)
	{
		if(file.annotations.get(NERCoreference.class) != null)
		{
			Optional<Annotation> nerCorefOpt = file.annotations.get(NERCoreference.class).stream().filter(ann -> ((NERCoreference)ann).mainCoref.element.entityName.toLowerCase().equals(map.element.entityName.toLowerCase())).findFirst();

			if(nerCorefOpt.isPresent())
			{
				NERCoreference nerc = (NERCoreference)nerCorefOpt.get();
				if(nerc.mainCoref.element.entityName.equals(map.element.entityName))
					nerc.addCoref(map);
			}else
			{
				NERCoreference coref = new NERCoreference(map);
				file.addAnnotation(NERCoreference.class, coref);
			}
		}else
		{
			NERCoreference coref = new NERCoreference(map);
			file.addAnnotation(NERCoreference.class, coref);
		}
	}
	
	public static NER getMainNERFromWord(TimeMLFile file, Word w)
	{
		LinkedList<Annotation> mainCorefNerAnnotation = file.annotations.get(NERCoreference.class) == null ? null : file.annotations.get(NERCoreference.class);
		if(mainCorefNerAnnotation != null)
		{
			for(Annotation corefNerAnnotation : mainCorefNerAnnotation)
			{
				NERCoreference currentNerCoref = (NERCoreference)corefNerAnnotation;
				if(currentNerCoref.mainCoref.element.word.equals(w))
					return currentNerCoref.mainCoref.element;
				
				for(EntityMapper<NER> relatedNER : currentNerCoref.coreferences)
				{
					if(relatedNER.element.word.equals(w))
						return currentNerCoref.mainCoref.element; 
				}
			}
		}
		
		return null;
		
	}
	
	public static NER getMainNerFromCoreference(TimeMLFile file, Coreference coref)
	{
		HashMap<Integer, LinkedList<NER>> nersInCoreference = new HashMap<Integer, LinkedList<NER>>();
		int numOfRelatedEntities = 0;
		nersInCoreference.put(numOfRelatedEntities, new LinkedList());
		Word next = coref.word;
		TokenizedSentence mainCorefSentence = TimeMLHelper.getWordSentence(file, next);
		for(int cri = 0; next != null && cri < coref.offset; cri++)
		{
			if(next.pos.equals("CC") || next.pos.equals(","))
			{
				numOfRelatedEntities++;
				nersInCoreference.put(numOfRelatedEntities, new LinkedList());
			}
			EntityMapper<Annotation> mainCorefNerAnnotation = mainCorefSentence.annotations.get(NER.class) == null ? null : mainCorefSentence.annotations.get(NER.class).get(next);
			if(mainCorefNerAnnotation != null)
			{
				NER currentCoreferenceNer = (NER)mainCorefNerAnnotation.element;
				
				if(nersInCoreference.get(numOfRelatedEntities).size() > 0
						&& nersInCoreference.get(numOfRelatedEntities).get(0).type.equals(currentCoreferenceNer.type)
						&& !nersInCoreference.get(numOfRelatedEntities).get(0).entityName.contains(currentCoreferenceNer.entityName))
				{
					NER existingNER = nersInCoreference.get(numOfRelatedEntities).get(0);
					existingNER.entityName = existingNER.entityName+"_"+currentCoreferenceNer.entityName;
					existingNER.offset = existingNER.offset + currentCoreferenceNer.offset;
				}else
				nersInCoreference.get(numOfRelatedEntities).add(currentCoreferenceNer);
				//if(next.pos.startsWith("NN"))
				//	A0Coref.add(next);
			}
			next = next.next;

		}
		
		NER lastNer = null;
		if(!nersInCoreference.get(0).isEmpty())
		{
			
			for(NER n : nersInCoreference.get(0))
			{
				if(n.type.equals(EntityType.PERSON))
					lastNer = n;
			}	
			
			if(lastNer == null)
				lastNer = nersInCoreference.get(0).getLast();
		}
		return lastNer;
	}
	
	public static Coreference getMainReference(TimeMLFile file, Word w)
	{
		Coreference mainCoreference = null;
		LinkedList<Annotation> corefs = file.annotations.get(Coreference.class);
		if(corefs == null)
			return null;
		for(Annotation annotation : corefs)
		{
			Coreference mainCoref = (Coreference) annotation;
			boolean containsWord = false;
			if(!mainCoref.word.equals(w))
			{
			
				for(Coreference c : mainCoref.coreferences)
				{
					if(c.word.equals(w))
						mainCoreference = mainCoref;
				}
			}else
			{
				mainCoreference = mainCoref;
			}
				
		}
		
		return mainCoreference;
	}
}
