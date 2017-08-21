package com.NLPFramework.Formatters.TimeML;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;

public class EventClassikFormatter extends FeaturesFormatter implements ISentenceFormatter
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



			if(sentence.annotations.get(Event.class) != null && sentence.annotations.get(Event.class).get(w) != null)
				mapper = sentence.annotations.get(Event.class).get(w);

			if(mapper != null)
			{
				if(w.sentencePosition >= mapper.firstWordPosition && w.sentencePosition <= mapper.endWordPosition)
				{
					if(w.sentencePosition == mapper.firstWordPosition)
						currentWordSb.append(PipesHelper.AppendPipes("B-event"));
					else
						currentWordSb.append(PipesHelper.AppendPipes("I-event"));
					
					if(!currentWordSb.toString().isEmpty())// && !mapper.element.eventClass.toString().equalsIgnoreCase("OCCURRENCE"))
					{	
						sb.append(currentWordSb.toString());
						sb.append(System.lineSeparator());
					}
				}
				else
				{
					currentWordSb.append(PipesHelper.AppendPipes("-"));
					mapper = null;
				}


			}else
			{
				currentWordSb.append(PipesHelper.AppendPipes("-"));
			}

		
		}	

		return sb.toString();
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "classik";
	}


}
