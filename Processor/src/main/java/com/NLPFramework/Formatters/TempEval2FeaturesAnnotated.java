package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEval2FeaturesAnnotated extends TempEval2FeaturesFormatter implements IWordFormatter {

	public String toString(Word w)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString(w));
		if(w.element_type.matches("timex"))
			 sb.append(PipesHelper.AppendPipes(w.time_IOB2));
			else if(w.element_type.matches("event"))
				sb.append(PipesHelper.AppendPipes(w.event_IOB2));
		 return sb.toString();
	}
	public boolean setValues(Word word, String values)
	{
		//super.setValues(word, values);
		String[] pipesArray = values.split("\\|");
		if(pipesArray.length > 22)
		{
			if(pipesArray[22].endsWith("timex3"))
				word.time_IOB2 = pipesArray[22];
			else if(pipesArray[22].endsWith("event"))
				word.event_IOB2 = pipesArray[22];
		}
		return true;
	}
	
	
	
}
