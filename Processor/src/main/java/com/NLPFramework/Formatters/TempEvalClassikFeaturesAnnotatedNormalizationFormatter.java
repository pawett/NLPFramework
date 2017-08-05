package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEvalClassikFeaturesAnnotatedNormalizationFormatter extends TempEvalClassikAnnotatedFormatter
		implements IWordFormatter {
	
	public boolean setValues(Word w, String values) 
	{
		//super.setValues(w, values);
		String[] pipesArray = values.split("\\|");
		 w.DCT = pipesArray[22];
		 w.ref_val= pipesArray[23];
		 w.value= pipesArray[24];
		 w.norm_type2 = pipesArray[25];
		 return true;
	}
	
	public String toString(Word w) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(w.isBeginElement())
		 {		 
			 sb.append(super.toString(w));
			 sb.append(PipesHelper.AppendPipes(w.DCT));	
			 sb.append(PipesHelper.AppendPipes(w.ref_val));
			 sb.append(PipesHelper.AppendPipes(w.value));
		 }
		 return sb.toString();
	}

}
