package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;

public interface INLPActionSentence {

	public  ArrayList<Class<? extends INLPActionSentence>> getDependencies();
	public TokenizedSentence execute(TokenizedSentence tokSentence);
}
