package com.NLPFramework.Formatters;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;

public class FeaturesEventAnnotatedFormatter  extends FeaturesFormatter implements ISentenceFormatter
{
	private String filter = null;
	
	public FeaturesEventAnnotatedFormatter()
	{
		
	}
	
	public FeaturesEventAnnotatedFormatter(String filter)
	{
		this.filter = filter;
	}
	
	public String toString(TokenizedSentence s, Word w)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString(w));
		
		if(s.annotations.get(Event.class)!=null && s.annotations.get(Event.class).get(w) != null)
			sb.append(PipesHelper.AppendPipes("B-event"));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		
		return sb.toString();
	}
	
	public String toString(TokenizedSentence sentence)
	{
		StringBuilder sb = new StringBuilder();
		StringBuilder currentWordSb = null;
		EntityMapper<Annotation> mapper = null;
	
		int verbPos = 0;
		for(Word v : sentence.verbs)
		{
			
			for(Word w : sentence)
			{
				
				String IOB = "-";
				currentWordSb = new StringBuilder();
				if(w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null)
				{
					currentWordSb.append(super.toString(w, v, w.semanticRoles.get(verbPos)));

				}else
				continue;

				if(filter == null ||  w.pos.contains(filter))
				{
					if(sentence.annotations.get(Event.class) != null && sentence.annotations.get(Event.class).get(w) != null)
						mapper = sentence.annotations.get(Event.class).get(w);

					if(mapper != null)
					{
						if(w.sentencePosition >= mapper.firstWordPosition && w.sentencePosition <= mapper.endWordPosition)
						{
							if(w.sentencePosition == mapper.firstWordPosition)
								IOB = "B-event";
							else
								IOB = "I-event";	
						}else
							mapper = null;
					}
				}

				currentWordSb.append(PipesHelper.AppendPipes(IOB));

				sb.append(currentWordSb.toString());
				//if(w.synt.equals("SBAR"))
				//sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
			}	
			verbPos++;
			sb.append(System.lineSeparator());
		}
		
		 return sb.toString();
	}

	@Override
	public void setValues(TokenizedSentence sentence, String values, Word firstElement)
	{
		String[]valuesArray = values.split("\\|");
		String IOBValue = valuesArray[valuesArray.length - 1];
		String wordPos = valuesArray[2];
		int position = Integer.parseInt(wordPos);
		Word currentWord =  sentence.get(position);
		if(IOBValue.matches("B-event"))
		{
			firstElement = currentWord;
			//sentence.events.put(firstElement, new ArrayList<>());
			EntityMapper<Event>eMap = new EntityMapper<>();
			Event event = new Event(firstElement);
			eMap.element = event;
			eMap.firstWordPosition = firstElement.sentencePosition;
			eMap.endWordPosition = firstElement.sentencePosition; 
			sentence.addAnnotation(Event.class, firstElement, eMap);
		}else if(IOBValue.matches("I-event") && firstElement != null)
		{
			//sentence.events.get(firstElement).add(currentWord);
			sentence.annotations.get(Event.class).get(firstElement).endWordPosition = currentWord.sentencePosition;
		}else
			firstElement = null;
	}
	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "eventFeatures";
	}


}
