package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;

public interface INLPActionFile {

	public  ArrayList<Class<? extends INLPActionFile>> getDependencies();
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception;
	
}
