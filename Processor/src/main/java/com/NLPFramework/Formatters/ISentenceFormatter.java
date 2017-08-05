package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;

public interface ISentenceFormatter 
{
	String toString(TokenizedSentence sentence);
	String toString(TokenizedSentence s, Word w);
	
	void setValues(TokenizedSentence sentence,  String values, Word firstElement);

	String getExtension();
}
