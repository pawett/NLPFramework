package com.NLPFramework.Formatters.TimeML;


import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.EventClass;

public class EventClassikAnnotatedFormatter extends FeaturesFormatter implements ISentenceFormatter 
{
	/*file|sent-num|tok-num|word|pos|syntbio|sentence|synt|verb|
	 * lemma|wn|roleconf|simplerolesIOB2|simplerolesIOB2_verb|
	 * simpleroles|depverb|tense|assertype|iobmainphrase|mainp-position|phra_id|PPdetail
	 */
	
	public String toString(TokenizedSentence sentence)
	{
		StringBuilder sb = new StringBuilder();
		EntityMapper<Annotation> mapper = null;
		
		
		for(Word w : sentence)
		{			
			StringBuilder currentWordSb = new StringBuilder();
			currentWordSb.append(super.toString(w));
			//getCurrentWordToString(w, currentWordSb);
			
			//sb.append(currentWordSb.toString());		
			
			if(sentence.annotations.get(Event.class) != null && sentence.annotations.get(Event.class).get(w) != null)
				mapper = sentence.annotations.get(Event.class).get(w);
			
			if(mapper != null)
			{
				Event element = (Event)mapper.element;
				if(w.sentencePosition >= mapper.firstWordPosition && w.sentencePosition <= mapper.endWordPosition)
				{
					if(w.sentencePosition == mapper.firstWordPosition)
						currentWordSb.append(PipesHelper.AppendPipes("B-event"));
					else
						currentWordSb.append(PipesHelper.AppendPipes("I-event"));
					
					if(element.eventClass == null || element.eventClass.toString().isEmpty())
						currentWordSb.append(PipesHelper.AppendPipes("-"));
					else
						currentWordSb.append(PipesHelper.AppendPipes(element.eventClass.toString()));
					
					if(!currentWordSb.toString().isEmpty())// && !mapper.element.eventClass.toString().equalsIgnoreCase("OCCURRENCE"))
					{	
						sb.append(currentWordSb.toString());
						sb.append(System.lineSeparator());
					}
					
				}
				else
				{
					currentWordSb.append(PipesHelper.AppendPipes("-"));
					currentWordSb.append(PipesHelper.AppendPipes("-"));
					mapper = null;
				}
				
				
			}else
			{
				currentWordSb.append(PipesHelper.AppendPipes("-"));
				currentWordSb.append(PipesHelper.AppendPipes("-"));
			}
			
		
			
		//	if(w.synt.equalsIgnoreCase("SBAR"))
			//	sb.append(System.lineSeparator());
			
		}	
		
		 return sb.toString();
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "classik";
	}

	@Override
	public void setValues(TokenizedSentence sentence, String values, Word firstElement) 
	{
		String[]valuesArray = values.split("\\|");
		String classValue = valuesArray[valuesArray.length - 1];
		String wordPos = valuesArray[2];
		int position = Integer.parseInt(wordPos);
		Word currentWord =  sentence.get(position);
		if(currentWord == null || sentence.annotations.get(Event.class) == null || sentence.annotations.get(Event.class).get(currentWord) == null)
		{
			Logger.Write("Error setting values for event classification");
			return;
		}
		Event event = (Event) sentence.annotations.get(Event.class).get(currentWord).element;
		event.eventClass = EventClass.valueOf(classValue);

		
	}


}
