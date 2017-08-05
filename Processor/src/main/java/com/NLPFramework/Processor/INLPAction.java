package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;

public interface INLPAction {

	public  ArrayList<Class<? extends INLPAction>> getDependencies();
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception;
}
