package com.NLPFramework.externalTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.joda.time.DateTime;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Timex3;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.TaggedWord;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations.*;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.tagger.common.Tagger;
import edu.stanford.nlp.time.*;
import edu.stanford.nlp.time.Timex.Range;
import edu.stanford.nlp.trees.Constituent;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.PropertiesUtils;
import edu.stanford.nlp.pipeline.*;

public class StanfordTimex implements ITextProcessor {
	
	public Date referenceDate = null;
	
	public StanfordTimex(Date referenceDate)
	{
		this.referenceDate = referenceDate;
	}
	
	public static TokenizedFile run(String filePath, Language lang)
	{
		File file = new File(filePath);
		TokenizedFile tokFile = new TokenizedFile(lang, file.getName());
		
		
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		//props.put("tokenize.whitespace", "true");
		//props.put("tokenize.keepeol", "false");
		//props.put("ssplit.eolonly", "false");
		//props.put("parse.originalDependencies", "true");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				/*PropertiesUtils.asProperties(
					"annotators", "tokenize,ssplit,pos,lemma",
					"tokenize.whitespace", "true",
					"tokenize.language", "en"));*/

	
		StringBuilder sb = new StringBuilder();
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lines.forEach(line -> sb.append(line+" "));
		
		Annotation doc = new Annotation(sb.toString());
		
		pipeline.annotate(doc);
	
		List<CoreMap> maps = doc.get(ParagraphsAnnotation.class);
		
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			
			TokenizedSentence tokSent = new TokenizedSentence();
			//SemanticGraph sg = s.get(SemanticGraphCoreAnnotations.class);
			
			// Tree maps2 = s.get(TreeAnnotation.class);
			// String p = maps2.pennString();
			// Set<Tree> c = maps2.subTrees();
			boolean isInsideTime = false;
			 for (CoreLabel token: s.get(TokensAnnotation.class))
			{
				
				List<String> test = token.get(PhraseWordsAnnotation.class);
				Word w = new Word();
				w.file = file.getName();
				w.sentenceNumber = token.get(SentenceIndexAnnotation.class);
				w.sentencePosition = token.get(IndexAnnotation.class)-1;
				w.word = token.get(OriginalTextAnnotation.class);
				w.lemma = token.get(LemmaAnnotation.class);
				w.pos = token.get(PartOfSpeechAnnotation.class);
				String ner = token.getString(NamedEntityTagAnnotation.class);
				if(ner.matches("DATE|TIME|PERIOD"))
				{
					w.semanticRole = "TMP";
					if(isInsideTime)
						w.semanticRoleIOB = "I";
					else
						w.semanticRoleIOB = "B";
				}else
				{
					isInsideTime = false;
				}
				tokSent.add(w);
				
			}
			 tokFile.add(tokSent);
			
		}
		return tokFile;
		
	}

	@Override
	public String runFromFile(String filePath) {
		File file = new File(filePath);
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, lemma, ner");
		//props.put("tokenize.whitespace", "true");
		//props.put("tokenize.keepeol", "false");
		//props.put("ssplit.eolonly", "false");
		//props.put("parse.originalDependencies", "true");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
				/*PropertiesUtils.asProperties(
					"annotators", "tokenize,ssplit,pos,lemma",
					"tokenize.whitespace", "true",
					"tokenize.language", "en"));*/

	
		StringBuilder sb = new StringBuilder();
		List<String> lines = null;
		try {
			lines = Files.readAllLines(file.toPath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lines.forEach(line -> sb.append(line+" "));
		
		Annotation doc = new Annotation(sb.toString());
		
		pipeline.annotate(doc);
	
		List<CoreMap> maps = doc.get(ParagraphsAnnotation.class);
		
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			
			TokenizedSentence tokSent = new TokenizedSentence();
			//SemanticGraph sg = s.get(SemanticGraphCoreAnnotations.class);
			System.out.println(s);
			// Tree maps2 = s.get(TreeAnnotation.class);
			// String p = maps2.pennString();
			// Set<Tree> c = maps2.subTrees();
			boolean isInsideTime = false;
			 for (CoreLabel token: s.get(TokensAnnotation.class))
			{
				
				List<String> test = token.get(PhraseWordsAnnotation.class);
				Word w = new Word();
				w.file = file.getName();
				w.sentenceNumber = token.get(SentenceIndexAnnotation.class);
				w.sentencePosition = token.get(IndexAnnotation.class)-1;
				w.word = token.get(OriginalTextAnnotation.class);
				w.lemma = token.get(LemmaAnnotation.class);
				w.pos = token.get(PartOfSpeechAnnotation.class);
				String ner = token.getString(NamedEntityTagAnnotation.class);
				if(ner.matches("DATE|TIME|PERIOD"))
				{
					w.semanticRole = "TMP";
					if(isInsideTime)
						w.semanticRoleIOB = "I";
					else
						w.semanticRoleIOB = "B";
				}else
				{
					isInsideTime = false;
				}
				tokSent.add(w);
				
			}
			//file.add(tokSent);
			
		}
		return null;
		
	}

	@Override
	public String runFromText(String text) 
	{
		
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();
		
		
		Annotation doc = new Annotation(text);
		if(this.referenceDate != null)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateValue = format.format(referenceDate);
			doc.set(edu.stanford.nlp.ling.CoreAnnotations.DocDateAnnotation.class, dateValue);
		}
		
		
		pipeline.annotate(doc);
	
		List<CoreMap> maps = doc.get(ParagraphsAnnotation.class);
		StringBuilder sb = new StringBuilder();
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			
			TokenizedSentence tokSent = new TokenizedSentence();
		
			boolean isInsideTime = false;
			 for (CoreLabel token: s.get(TokensAnnotation.class))
			{
				String sent_num = String.valueOf(token.get(SentenceIndexAnnotation.class));
				String tok_num = String.valueOf(token.get(IndexAnnotation.class)-1);
				String word = token.get(OriginalTextAnnotation.class);
				sb.append(sent_num);
				sb.append(PipesHelper.AppendPipes(tok_num));
				sb.append(PipesHelper.AppendPipes(word));
				Timex t = token.get(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class);
				String ner = token.getString(NamedEntityTagAnnotation.class);
				String nerNormalized = token.get(NormalizedNamedEntityTagAnnotation.class);
				//String ner2 = s.get(TimexAnnotations.class);
				if(ner.matches("DATE|TIME|DURATION|SET"))
				{
					String value = nerNormalized;
					if(value == null && t != null && t.range()!= null)
						value = t.range().duration;
					if(value == null && t != null && t.value() != null && !t.value().isEmpty())
						value = t.value();
					if(isInsideTime)
					{
						sb.append(PipesHelper.AppendPipes("I-timex"));
						sb.append(PipesHelper.AppendPipes(ner));
						
					}
					else
					{
						sb.append(PipesHelper.AppendPipes("B-timex"));
						sb.append(PipesHelper.AppendPipes(ner));
						isInsideTime = true;
					}
					
					if(value!= null && !value.isEmpty())
						sb.append(PipesHelper.AppendPipes(value));
					else
						sb.append(PipesHelper.AppendPipes("-"));
					
				}else
				{
					isInsideTime = false;
					sb.append(PipesHelper.AppendPipes("O"));
					sb.append(PipesHelper.AppendPipes("-"));
					sb.append(PipesHelper.AppendPipes("-"));
				}
				sb.append(System.lineSeparator());
			}
			
		}
		return sb.toString();
	}

	@Override
	public void runFromSentence(TokenizedSentence sentence) {
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();
		
		
		Annotation doc = new Annotation(sentence.toString());
		if(this.referenceDate != null)
		{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			String dateValue = format.format(referenceDate);
			doc.set(edu.stanford.nlp.ling.CoreAnnotations.DocDateAnnotation.class, dateValue);
		}
		
		
		pipeline.annotate(doc);
	

		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			
				
			boolean isInsideTime = false;
			Word firstElement = null;
			 for (CoreLabel token: s.get(TokensAnnotation.class))
			{
				int tok_num = token.get(IndexAnnotation.class)-1;
				
				Word currentWord = sentence.get(tok_num);
				String word = token.get(OriginalTextAnnotation.class);
				//Logger.WriteDebug("Original word: " + currentWord + ". SF Word: " + word);
				Timex t = token.get(edu.stanford.nlp.time.TimeAnnotations.TimexAnnotation.class);
				String ner = token.getString(NamedEntityTagAnnotation.class);
				String nerNormalized = token.get(NormalizedNamedEntityTagAnnotation.class);
				//String ner2 = s.get(TimexAnnotations.class);
				
				if(ner.matches("DATE|TIME|DURATION|SET"))
				{
					String value = nerNormalized;
					if(value == null && t != null && t.range()!= null)
						value = t.range().duration;
					if(value == null && t != null && t.value() != null && !t.value().isEmpty())
						value = t.value();
					
					
					if(firstElement == null)
					{

						currentWord.time_IOB2 = "B-timex";
						currentWord.element_type = "timex";
						currentWord.norm_type2 = ner;

						isInsideTime = true;


						firstElement = currentWord;
						EntityMapper<Timex3>eMap = new EntityMapper<>();
						Timex3 timex = new Timex3(firstElement);
						eMap.element = timex;
						eMap.firstWordPosition = firstElement.sentencePosition;
						eMap.endWordPosition = firstElement.sentencePosition; 
						sentence.addAnnotation(timex.getClass(), firstElement, eMap);
						

					}			
					if(firstElement != null)
					{
						currentWord.time_IOB2 = "I-timex";
						currentWord.element_type = "timex";
						currentWord.norm_type2 = ner;
						sentence.annotations.get(Timex3.class).get(firstElement).endWordPosition = currentWord.sentencePosition;
					}
				
					
					if(value!= null && !value.isEmpty())
						currentWord.norm_type2_value = value;
					else
						currentWord.norm_type2_value = "-";
					
				}else
				{
					//isInsideTime = false;
					currentWord.time_IOB2 = "O";
					firstElement = null;
				}

			}
			
		}
		
	}

}
