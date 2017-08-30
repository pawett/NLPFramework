package com.NLPFramework.Domain;

import java.util.ArrayList;

import com.NLPFramework.TimeML.Domain.EntityMapper;

public class NERCoreference extends Annotation {

	private static final long serialVersionUID = 1L;
	public int offset = 0;
	public EntityMapper<NER> mainCoref = null;
	public ArrayList<EntityMapper<NER>> coreferences = new ArrayList<>();
	public NERCoreference(EntityMapper<NER> nerMap)
	{
		mainCoref = nerMap;
	}
	
	public void addCoref(EntityMapper<NER> nerMap)
	{
		NER ner = ((NER)nerMap.element);
		
		if(ner != null && mainCoref.element.entityName.toLowerCase().equals(ner.entityName.toLowerCase()) && !coreferences.stream().anyMatch(c -> c.element.word.equals(ner.word)))
			coreferences.add(nerMap);
	}
	
	public String printCurrent()
	{
		return mainCoref.element.entityName + " in sentence " + mainCoref.element.word.sentenceNumber;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(printCurrent());
		
		sb.append(" ==> ");
				
		if(coreferences.size() > 0)
		{
			
			int numCoref = 0;
			for(EntityMapper<NER> c : coreferences)
			{
				if(numCoref > 0)
					sb.append(", ");
				sb.append(c.element.entityName + " in sentence " + c.element.word.sentenceNumber);
				numCoref++;
			}
		}
		
		
		return sb.toString();
	}

}
