package com.NLPFramework.Processor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Helpers.ProgramDirectoryUtil;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class Configuration 
{

	private static Language language = null;
	
	
	public static Language getLanguage()
	{
		return language;
	}
	
	public static void setLanguage(Language lang)
	{
		language = lang;
	}
	
	private static NLPProcessorAction action = NLPProcessorAction.ANNOTATE;
	
	public static NLPProcessorAction getAction()
	{
		return action;
	}
	
	public static void setAction(NLPProcessorAction act)
	{
		action = act;
	}
	
	private static String folderPath = null;
	
	public static String getFolderPath()
	{
		return folderPath;
	}
	public static void setFolderPath(String path)
	{
		folderPath = path;
	}
	
	private static IMachineLearningMethod mlMethod = null;
	public static IMachineLearningMethod getMachineLearningMethod()
	{
		return mlMethod;
	}
	public static void setMachineLearningMethod(IMachineLearningMethod method)
	{
		mlMethod = method;
	}
	
	private static String DCT = null;
	public static String getDCT()
	{
		return DCT;
	}
	public static void setDCT(String dct)
	{
		DCT = dct;
	}
	
	private static String approach = "TIPSem";
	public static String getApproach()
	{
		return approach;
	}
	public static void setAproach(String app)
	{
		approach = app;
	}
	
	
	private static String modelsPath = ProgramDirectoryUtil.getProgramDirectory() + File.separator + "program-data";
	public static String getModelsPath()
	{
		return modelsPath;
	}
	public static void setModelsPath(String path)
	{
		modelsPath = path;
	}
	
	public static INLPAction getClassForAction(Class<? extends INLPAction> actionClass, String filePath)
	{
		Map<Class<? extends INLPAction>, INLPAction> mapping = new HashMap<>();//<ArrayList<Class<? extends INLPAction>>, INLPAction>();
		
		mapping.put(ActionSentenceSplitterBase.class, new ActionSentenceSplitterCoreNLP(filePath));
		mapping.put(ActionTokenizerBase.class, new ActionTokenizerCoreNLP());
		mapping.put(ActionNERBase.class, new ActionNERSenna());
		mapping.put(ActionPOSBase.class, new ActionPOSCoreNLP());
		mapping.put(ActionLemmatizerBase.class, new ActionLemmatizerCoreNLP());
		mapping.put(ActionSintacticParserBase.class, new ActionSintacticParserSenna());
		mapping.put(ActionSemanticParserBase.class, new ActionSemanticParserSenna());
		
		return mapping.get(actionClass);
		
	}
	
}
