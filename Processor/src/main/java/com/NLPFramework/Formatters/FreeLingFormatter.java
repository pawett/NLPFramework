package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class FreeLingFormatter implements IWordFormatter {

	
	public String toString(Word w) {
		StringBuilder sb = new StringBuilder();
		 sb.append(w.word);
		 sb.append(PipesHelper.AppendPipes(w.lemma));
		 sb.append(PipesHelper.AppendPipes(w.pos));
		 return sb.toString();
	}

	
	public boolean setValues(Word w, String values) {
		String [] pipesArray = values.split("\\|");
		w.word = pipesArray[0];
	 	w.lemma = pipesArray[1];
	 	w.pos = pipesArray[2];
	 	return true;
	}


	@Override
	public boolean isSentenceEnd(String text) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public String getExtension() {
		return "freeling";
	}
	
}
