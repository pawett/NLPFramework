package com.NLPFramework.externalTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.TimeType;
import com.NLPFramework.TimeML.Domain.Timex3;

import edu.stanford.nlp.ie.util.RelationTriple;
import edu.stanford.nlp.ling.CoreAnnotations.IndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.NormalizedNamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.OriginalTextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentenceIndexAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.naturalli.NaturalLogicAnnotations;
import edu.stanford.nlp.naturalli.SentenceFragment;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.time.Timex;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNER implements ITextProcessor {
	
	public Date referenceDate = null;
	
	public StanfordNER(Date referenceDate)
	{
		this.referenceDate = referenceDate;
	}

	@Override
	public String runFromText(String text) 
	{
		
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getNERPipeLine();	
		
		Annotation doc = new Annotation(text);
				
		pipeline.annotate(doc);
	
		
		StringBuilder sb = new StringBuilder();
		for(CoreMap s : doc.get(SentencesAnnotation.class))
		{
			 Collection<RelationTriple> triples = s.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
		      // Print the triples
		      for (RelationTriple triple : triples) {
		        Logger.WriteDebug(triple.confidence + "\t" +
		            triple.subjectLemmaGloss() + "\t" +
		            triple.relationLemmaGloss() + "\t" +
		            triple.objectLemmaGloss());
		      }

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
		StanfordCoreNLP pipeline = StanfordCoreSingleton.getNERPipeLine();
		
		
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
			// Logger.WriteDebug(s.toString());
			//Collection<RelationTriple> triples = s.get(NaturalLogicAnnotations.RelationTriplesAnnotation.class);
			//Collection<SentenceFragment> fragments=  s.get(NaturalLogicAnnotations.EntailedSentencesAnnotation.class);
		      // Print the triples
		  /*    for (RelationTriple triple : triples)
		      {
		    	  if(!triple.isPrefixBe() && ! triple.isSuffixBe())
		    		  continue;
		     //   Logger.WriteDebug(triple.confidence + "\t" +
		      //      triple.subject + "\t" +
		      //      triple.relationLemmaGloss() + "\t" +
		      //      triple.objectLemmaGloss());
		      }
			

				*/
			
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

						firstElement = currentWord;
						
						EntityMapper<Timex3>eMap = new EntityMapper<>();
						Timex3 timex = new Timex3(firstElement);
						eMap.element = timex;
						eMap.firstWordPosition = firstElement.sentencePosition;
						eMap.endWordPosition = firstElement.sentencePosition;
						timex.type = TimeType.valueOf(ner);
						timex.value = value;
						if(t != null && t.beginPoint() == 0)
							timex.anchorTimeID = "t0";
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

	@Override
	public String runFromFile(String filePath) {
		// TODO Auto-generated method stub
		return null;
	}

}
