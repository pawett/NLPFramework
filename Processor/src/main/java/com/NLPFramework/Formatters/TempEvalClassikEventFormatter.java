package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;

public class TempEvalClassikEventFormatter extends TempEvalClassikFormatter implements ISentenceFormatter {

	@Override
	public String toString(Word w) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(w.isBeginElement() && w.element_type.matches("event"))
		 {	
			sb.append(super.toString(w));
						
		 }
		 return sb.toString();
	}

	@Override
	public String toString(TokenizedSentence sentence) {
		/*if(mapper == null)
			mapper = sentence.eventsMappings.get(w);
		
		if(mapper != null)
		{
			if(w.sentencePosition >= mapper.firstWordPosition && w.sentencePosition <= mapper.endWordPosition)
			{
				if(w.sentencePosition == mapper.firstWordPosition)
					sb.append(PipesHelper.AppendPipes("B-event"));
				else
					sb.append(PipesHelper.AppendPipes("I-event"));
			}else
				mapper = null;
		}
		sb.append(System.lineSeparator());*/
		return null;
	}

	@Override
	public void setValues(TokenizedSentence sentence, String values, Word firstElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString(TokenizedSentence s, Word w) {
		// TODO Auto-generated method stub
		return null;
	}


}
