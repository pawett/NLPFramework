package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;

public abstract class ActionLemmatizerBase implements INLPActionFile {

	@Override
	public ArrayList<Class<? extends INLPActionFile>> getDependencies()
	{
		ArrayList<Class<? extends INLPActionFile>> dependencies = new ArrayList<>();
		dependencies.add(ActionTokenizerBase.class);
		
		return dependencies;
	}

	@Override
	public abstract TokenizedFile execute(TokenizedFile tokFile) throws Exception;

}
