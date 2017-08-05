package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TreeTagFormatter implements IWordFormatter {

	public String toString(Word w) {
		StringBuilder sb = new StringBuilder();
		 sb.append(w.word);
		 sb.append(PipesHelper.AppendPipes(w.pos));
		 sb.append(PipesHelper.AppendPipes(w.lemma));
		 return sb.toString();
	}

	
	public boolean setValues(Word w, String values) {
		String [] pipesArray =values.replaceAll("\\|", "-").split("\t");
		w.word = pipesArray[0];
		if(!pipesArray[1].equalsIgnoreCase("SENT"))
			w.pos = pipesArray[1];	
		else
			w.pos = w.word;
		w.lemma = pipesArray[2];
		return true;
	}


	@Override
	public boolean isSentenceEnd(String text) {
		String [] values = text.split("\t");
		if(values[1].equalsIgnoreCase("SENT"))return true;
		return false;
	}


	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "treetag";
	}

}
