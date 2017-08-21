package com.NLPFramework.Formatters.TimeML;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Timex3;

public class FeaturesTimexAnnotatedFormatter  extends FeaturesFormatter implements ISentenceFormatter
{
	
	public String toString(TokenizedSentence sentence)
	{
		StringBuilder sb = new StringBuilder();
		StringBuilder currentWordSb = null;
		EntityMapper<Annotation> mapper = null;
	
		for(Word w : sentence)
		{
			currentWordSb = new StringBuilder(super.toString(w));
			String IOB = "-";
			
			if(sentence.annotations.get(Timex3.class).get(w) != null)
				mapper = sentence.annotations.get(Timex3.class).get(w);
			
			if(mapper != null)
			{
				if(w.sentencePosition >= mapper.firstWordPosition && w.sentencePosition <= mapper.endWordPosition)
				{
					if(w.sentencePosition == mapper.firstWordPosition)
						IOB = "B-timex";
					else
						IOB = "I-timex";	
				}else
					mapper = null;			
				
			}
			
			currentWordSb.append(PipesHelper.AppendPipes(IOB));
			sb.append(currentWordSb.toString());
			sb.append(System.lineSeparator());
		}	
		
		 return sb.toString();
	}
	
	
	public void setValues(TokenizedSentence sentence, String values, Word firstElement)
	{
		String[]valuesArray = values.split("\\|");
		String IOBValue = valuesArray[valuesArray.length - 1];
		String wordPos = valuesArray[2];
		int position = Integer.parseInt(wordPos);
		Word currentWord =  sentence.get(position);
		if(IOBValue.matches("B-timex"))
		{
			firstElement = currentWord;
			//sentence.times.put(firstElement, new ArrayList<>());
			EntityMapper<Timex3>eMap = new EntityMapper<>();
			Timex3 timex = new Timex3(firstElement);
			eMap.element = timex;
			eMap.firstWordPosition = firstElement.sentencePosition;
			eMap.endWordPosition = firstElement.sentencePosition; 
			sentence.addAnnotation(timex.getClass(), firstElement, eMap);
		}else if(IOBValue.matches("I-timex") && firstElement != null)
		{
			//sentence.times.get(firstElement).add(currentWord);
			sentence.annotations.get(Timex3.class).get(firstElement).endWordPosition = currentWord.sentencePosition;
		}else
			firstElement = null;
	}
	
	public String toString(TokenizedSentence s, Word w)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString(w));
		String timexIOB = "-";
		if(s.annotations.get(Timex3.class) != null)
		{
			for(Word firstElement : s.annotations.get(Timex3.class).keySet())
			{
				EntityMapper<Annotation> map = s.annotations.get(Timex3.class).get(firstElement);
				if(w.sentencePosition == map.firstWordPosition)
					timexIOB = "B-timex";
				else if(w.sentencePosition > map.firstWordPosition && w.sentencePosition <= map.endWordPosition)
					timexIOB ="I-timex";
			}
		}
		
		sb.append(PipesHelper.AppendPipes(timexIOB));
		
		return sb.toString();
	}

	@Override
	public String getExtension() 
	{
		return "timexFeatures";
	}


}
