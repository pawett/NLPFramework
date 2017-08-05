package com.NLPFramework.externalTools;

import com.NLPFramework.Domain.TokenizedSentence;

public interface ITextProcessor
{
	public String runFromFile(String filePath);
	public String runFromText(String text);
	public void runFromSentence(TokenizedSentence sentence);
}
