package com.NLPFramework.externalTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;

import edu.stanford.nlp.coref.CorefCoreAnnotations;
import edu.stanford.nlp.coref.data.CorefChain;
import edu.stanford.nlp.ling.CoreAnnotations.IndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.OriginalTextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.ParagraphsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PhraseWordsAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentenceIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.semgraph.SemanticGraph;
import edu.stanford.nlp.semgraph.SemanticGraphCoreAnnotations.EnhancedPlusPlusDependenciesAnnotation;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;

public class StanfordSynt implements ITextProcessor {
	

	
	public StanfordSynt()
	{
		
	}
	
	public static LinkedList<String> tokenize(String text)
	{
		LinkedList<String> tokenizedArray = new LinkedList<>();
		Annotation doc = new Annotation(FileHelper.formatText(text));
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getPipeLine();
		pipeline.annotate(doc);
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			 for (CoreLabel token: s.get(TokensAnnotation.class))
				{
				 	String word = token.get(TextAnnotation.class);
				 	tokenizedArray.add(word);
				}
		}
		return tokenizedArray;
	}
	
	public static TokenizedFile run(String filePath, Language lang)
	{
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getNERPipeLine();
		File file = new File(filePath);
		TokenizedFile tokFile = new TokenizedFile(lang, file.getName());
		
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
		
		Annotation doc = new Annotation(sb.toString());
		tokFile.setOriginalText(sb.toString());
		pipeline.annotate(doc);
	
		//List<CoreMap> maps = doc.get(ParagraphsAnnotation.class);
		
		/* for (CorefChain cc : doc.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		      System.out.println("\t" + cc);
		      System.out.println("\t" + cc.getMentionsInTextualOrder());
		    }
		    for (CoreMap sentence : doc.get(CoreAnnotations.SentencesAnnotation.class)) {
		      System.out.println("---");
		      System.out.println("mentions");
		      for (Mention m : sentence.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
		        System.out.println("\t" + m.getRelation());
		        System.out.println("\t" + m.getPosition());
		        System.out.println("\t" + m.endIndex);
		        System.out.println("\t" + m.startIndex);
		        System.out.println("\t" + m.nerString);
		        System.out.println("\t" + m.paragraph);
		        System.out.println("\t" + m.contextParseTree);
		        System.out.println("\t" + m.dependingVerb);
		        System.out.println("\t" + m);
		       }
		    }
		  */
		
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			  
			TokenizedSentence tokSent = new TokenizedSentence();
			tokSent.originalText = s.get(TextAnnotation.class);
		
			//SemanticGraph dependencies = s.get(EnhancedPlusPlusDependenciesAnnotation.class);
			
		/*	for (Mention m : s.get(CorefCoreAnnotations.CorefMentionsAnnotation.class)) {
				tokSent.addAnnotation(Mention.class, null, null);
		       }*/
			
			
			boolean isInsideTime = false;
			int tokNum = 0;
			 for (CoreLabel token: s.get(TokensAnnotation.class))
			{
				String word = token.get(TextAnnotation.class);
				String[] words = word.split("\\s+");
				//int tokNum = token.get(IndexAnnotation.class)-1;
				//for(int i = 0; i < words.length; i++)
				//{
			
					Word w = new Word();
					w.file = file.getName();
					w.sentenceNumber = token.get(SentenceIndexAnnotation.class);
					w.sentencePosition = tokNum;
					w.word = word;
					w.lemma = token.get(LemmaAnnotation.class);
					w.pos = token.get(PartOfSpeechAnnotation.class);
					w.ner = token.getString(NamedEntityTagAnnotation.class);
					tokSent.add(w);
					tokNum++;
				//}
				/*String ner = token.getString(NamedEntityTagAnnotation.class);
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
				}*/
				
				
			}
				Tree tree = s.get(TreeAnnotation.class);
				//setSyntacticTree(tree.firstChild(), tokSent);
			 tokFile.add(tokSent);
			
		}
		
		/* for (CorefChain cc : doc.get(CorefCoreAnnotations.CorefChainAnnotation.class).values()) {
		     // System.out.println("\t" + cc);
		     // System.out.println("\t" + cc.getMentionsInTextualOrder());
		      Coreference mainCoref = null;
		      for(IntPair key : cc.getMentionMap().keySet())
		      {
		    	  
		    	 Set<CorefChain.CorefMention> mentions = cc.getMentionMap().get(key);
		    	 for(CorefChain.CorefMention m : mentions)
		    	 {
		    		 Word w = tokFile.get(m.sentNum - 1).get(m.startIndex -1);
		    		 int offset = (m.endIndex - m.startIndex) - 1;
		    		 Coreference coref = new Coreference(w, offset);
		    		 if(mainCoref == null)
		    		 {
		    			 mainCoref = coref;
		    			 tokFile.addAnnotation(Coreference.class, coref);
		    		 }
		    		 else
		    			 mainCoref.addCoref(coref);
		    		 
		    		
		    	 }
		      }
		    }*/
		
		return tokFile;
		
	}

	
	private static void setSyntacticTree(Tree tree, TokenizedSentence tokSent) 
	{
		for(Tree subTree : tree.children())
		{
			TokenizedSentence subSent = new TokenizedSentence();
			subSent.synt = subTree.label().value();
			tokSent.subSentences.add(subSent);
			if(!subTree.isPreTerminal())
			setSyntacticTree(subTree, subSent);
			else
			{
				for(Label lb : subTree.yield())
				{
					//lb.
				}
			}
		}
		
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
		
		return null;
	}

	@Override
	public void runFromSentence(TokenizedSentence sentence) {
		return ;
		
	}

}
