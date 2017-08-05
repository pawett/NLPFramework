package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEvalClassikFeaturesAnnotatedNormalizationNormalizedFormatter
		extends TempEvalClassikFeaturesAnnotatedNormalizationFormatter implements IWordFormatter {

	public boolean setValues(Word w, String values) 
	{
		//super.setValues(w, values);
		String[] pipesArray = values.split("\\|");
		w.norm_type2 = pipesArray[25];
		return true;
	}
	
	public String toString(Word w) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(w.isBeginElement())
		 {		 
			 sb.append(super.toString(w));
			 sb.append(PipesHelper.AppendPipes(w.norm_type2));
		 }
		 return sb.toString();
	}
}
