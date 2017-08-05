package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEvalClassikAnnotatedFormatter extends TempEvalClassikFormatter implements IWordFormatter {

	public boolean setValues(Word w, String values) 
	{
		//super.setValues(w, values);
		String[] pipeArray = values.split("\\|");
		w.element_type= pipeArray[20];
		w.element_type_class= pipeArray[22];
		return true;
	}
	
	public String toString(Word w) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(w.isBeginElement())
		 {		 
			 sb.append(super.toString(w));
			 sb.append(PipesHelper.AppendPipes(w.element_type_class));			
		 }
		 return sb.toString();
	}
}
