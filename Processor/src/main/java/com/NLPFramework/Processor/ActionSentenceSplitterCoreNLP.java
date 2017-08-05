package com.NLPFramework.Processor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.externalTools.CoreNLPSentenceSplitter;
import com.NLPFramework.externalTools.StanfordCoreSingleton;
import com.NLPFramework.externalTools.StanfordSynt;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class ActionSentenceSplitterCoreNLP extends ActionSentenceSplitterBase {

	
	public ActionSentenceSplitterCoreNLP(String filePath) {
		super(filePath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception 
	{

		File file = new File(filePath);
		tokFile = new TokenizedFile(Configuration.getLanguage(), file.getName());
		
		StringBuilder sb = new StringBuilder();
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//.replace("''", "\"").replace("``","\"") + " "
		lines.forEach(line -> sb.append(FileHelper.formatText(line) + " "));
		
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();
		Annotation doc = new Annotation(sb.toString());
		
		pipeline.annotate(doc);
			
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{			
			TokenizedSentence tokSent = new TokenizedSentence();
			tokSent.originalText = s.get(TextAnnotation.class);
			tokFile.add(tokSent);
		}
		return tokFile;
	}


}
