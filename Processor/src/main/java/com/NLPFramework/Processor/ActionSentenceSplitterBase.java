package com.NLPFramework.Processor;

import java.io.File;
import java.util.ArrayList;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.externalTools.StanfordSynt;

public abstract class ActionSentenceSplitterBase implements INLPAction 
{

	protected String filePath = null;
	public ActionSentenceSplitterBase(String filePath)
	{
		this.filePath = filePath;
	}
	
	@Override
	public abstract TokenizedFile execute(TokenizedFile tokFile) throws Exception;
	

	@Override
	public ArrayList<Class<? extends INLPAction>> getDependencies()
	{
		return null;
	}

}
