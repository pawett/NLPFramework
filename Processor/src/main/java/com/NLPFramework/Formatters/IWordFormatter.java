package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;

public interface IWordFormatter {
	public String splitRegex = "\t";
	public String getExtension();
	public String toString(Word w);
	public boolean setValues(Word word, String values);
	public boolean isSentenceEnd(String text);
}
