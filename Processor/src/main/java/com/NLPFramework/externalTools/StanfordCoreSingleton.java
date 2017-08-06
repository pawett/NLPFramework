package com.NLPFramework.externalTools;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class StanfordCoreSingleton {

	private static StanfordCoreNLP tokenizerPipeline = null;
	private static StanfordCoreNLP timexPipeline = null;
	private static StanfordCoreNLP NERPipeline = null;
	
	public static StanfordCoreNLP getPipeLine()
	{
		
		if(tokenizerPipeline == null)
		{
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit");
			props.put("threads", "4");
		//ner
			//quote
			//props.put("tokenize.whitespace", "false");
		//	props.put("tokenize.options", "strictTreebank3=true");
			//props.put("quote.unicodeQuotes", "true");
			
			//props.put("ner.useSUTime", "true");
			//props.put("sutime.markTimeRanges", "false");
			//props.put("sutime.includeRange", "false");
		
			tokenizerPipeline = new StanfordCoreNLP(props);
		}
		return tokenizerPipeline;
		//return getNERPipeLine();
	}
	
	public static StanfordCoreNLP getTimexPipeLine()
	{
		if(timexPipeline == null)
		{
			Properties props = new Properties();
			props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
			props.put("ner.useSUTime", "true");
			props.put("sutime.markTimeRanges", "true");
			props.put("sutime.includeRange", "true");
			props.put("threads", "4");
			timexPipeline = new StanfordCoreNLP(props);
		}
		return timexPipeline;
	}
	
	private static StanfordCoreNLP CoreferencePipeline = null;
	public static StanfordCoreNLP getCoreferencePipeLine()
	{
		if(CoreferencePipeline == null)
		{
			Properties props = new Properties();
			// tokenize, ssplit, pos, lemma, ner, parse, depparse, mention, dcoref, coref, natlog, openie
			props.put("annotators", "tokenize,ssplit,pos,lemma,ner,parse,mention,coref");//,ner,parse,mention,coref");
			//props.put("openie.resolve_coref", "true");
			props.put("coref.algorithm", "neural");
			props.put("coref.neural.greedyness", "0.5");
			//props.put("openie.triple.strict" ,"true");
			//props.put("ner.useSUTime", "true");
			//props.put("sutime.markTimeRanges", "true");
			//props.put("sutime.includeRange", "true");
			props.put("threads", "4");
			//props.put("openie.threads", "4");
			CoreferencePipeline = new StanfordCoreNLP(props);
		}
		return CoreferencePipeline;
	}
	
	
	public static StanfordCoreNLP getNERPipeLine()
	{
		if(NERPipeline == null)
		{
			Properties props = new Properties();
			// tokenize, ssplit, pos, lemma, ner, parse, depparse, mention, dcoref, coref, natlog, openie
			props.put("annotators", "tokenize,ssplit,pos,lemma, ner");//, parse, depparse, mention, coref");//,ner,parse,mention,coref");
			//props.put("openie.resolve_coref", "true");
			//props.put("coref.algorithm", "statistical");
			//props.put("openie.triple.strict" ,"true");
			props.put("ner.useSUTime", "true");
			props.put("sutime.markTimeRanges", "true");
			props.put("sutime.includeRange", "true");
			props.put("threads", "4");
			//props.put("openie.threads", "4");
			NERPipeline = new StanfordCoreNLP(props);
		}
		return NERPipeline;
	}
}
