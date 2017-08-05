package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;

public abstract class ActionTokenizerBase implements INLPAction {

	@Override
	public ArrayList<Class<? extends INLPAction>> getDependencies()
	{
		ArrayList<Class<? extends INLPAction>> dependencies = new ArrayList<>();
		dependencies.add(ActionSentenceSplitterBase.class);
		
		return dependencies;
	}

	@Override
	public abstract TokenizedFile execute(TokenizedFile tokFile) throws Exception;

}
