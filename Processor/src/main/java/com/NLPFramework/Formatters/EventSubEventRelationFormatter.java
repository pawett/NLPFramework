package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

public class EventSubEventRelationFormatter  implements IFileFormatter 
{
	
	
	/* 0 file| 1 lid | 2 eid | 3 tid |    
	  4 e_class| 5 e_pos| 6 e_token| 7 e_tense | 8 e_tense-aspect|
	   9 e_govPP| 10 e_govTMPSub| 11 t_type| 12 t_ref| 13 t_govPP|
	    14 t_govTMPSub|15 synt_relation
	 */
	//exampletext2.txt|l3|ei2|t2|
	//OCCURRENCE|VERB|buy|FUTURE|FUTURE|FUTURE-NONE|-|-|DATE|reference|-|-|samephra
	public String toString(TimeMLFile file)
	{
		StringBuilder sb = new StringBuilder();
		
		for(Object tlObject : file.annotations.get(TimeLink.class))
		{
			String annotation = annotateTimeLink(file, tlObject);
			if(annotation != null)
			{
				sb.append(annotation);
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
			}
		}
		
	
		return sb.toString();		
	}
	
	public String annotateTimeLink(TimeMLFile file, Object tlObject)
	{
		StringBuilder sb = new StringBuilder();
		TimeLink tl = (TimeLink)tlObject;// file.annotations.get(TimeLink.class).get(id);
				
		MakeInstance mkInstance = tl.eventInstance;
		MakeInstance mkRelatedInstance = tl.relatedToEventInstance;
		
		if(mkInstance == null || mkRelatedInstance == null || mkInstance.event.word.sentenceNumber != mkRelatedInstance.event.word.sentenceNumber)
			return null;
		
		TokenizedSentence sentence = file.get( mkInstance.event.word.sentenceNumber);
		Word mainVerb = TimeMLHelper.getSentenceMainEvent(sentence);
		
		if(mkInstance.event.word.isVerb == true && mkRelatedInstance.event.word.isVerb == true &&  !mkInstance.event.word.equals(mainVerb) &&  !mkRelatedInstance.event.word.equals(mainVerb))
			return null;
		
		
		sb.append(file.getName());
		sb.append(PipesHelper.AppendPipes(tl.id));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.eventClass));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.lemma));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.pos));
		Word depVerb = file.get( mkInstance.event.word.sentenceNumber).getWordDependantVerb( mkInstance.event.word);
		if(depVerb != null && !depVerb.tense.equals("-"))
		{
			sb.append(PipesHelper.AppendPipes(depVerb.tense));
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventTenseFromWordTense(depVerb.tense) + "_" + TimeMLHelper.getEventAspectFromTense(depVerb.tense)));			
		}
		else
		{
			sb.append(PipesHelper.AppendPipes(mkInstance.event.tense));
			sb.append(PipesHelper.AppendPipes(mkInstance.event.aspect));
		}
		
				
		if(mkInstance.event.word != null && sentence.getWordPP(mkInstance.event.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(mkInstance.event.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
	
		//////////////////////////////////////////////////////////
					
		sb.append(PipesHelper.AppendPipes(mkRelatedInstance.event.eventClass));
		sb.append(PipesHelper.AppendPipes(mkRelatedInstance.event.word.lemma));
		sb.append(PipesHelper.AppendPipes(mkRelatedInstance.event.word.pos));
		depVerb = null;
		depVerb = file.get(mkRelatedInstance.event.word.sentenceNumber).getWordDependantVerb( mkRelatedInstance.event.word);
		if(depVerb != null && !depVerb.tense.equals("-"))
		{
			sb.append(PipesHelper.AppendPipes(depVerb.tense));
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventTenseFromWordTense(depVerb.tense) + "_" + TimeMLHelper.getEventAspectFromTense(depVerb.tense)));
				
		}
		else
		{
			sb.append(PipesHelper.AppendPipes(mkRelatedInstance.event.tense));
			sb.append(PipesHelper.AppendPipes(mkRelatedInstance.event.aspect));
		}
		
				
		if(mkInstance.event.word != null && sentence.getWordPP(mkRelatedInstance.event.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(mkRelatedInstance.event.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		
		
		Timex3 relatedTime = TimeMLHelper.getTimexByDependantVerb(file, mkInstance.event.word.depverb);
		if(relatedTime == null)
			relatedTime = file.getDCT();
		Timex3 relatedMkInstanceTime = TimeMLHelper.getTimexByDependantVerb(file, mkRelatedInstance.event.word.depverb);
		if(relatedMkInstanceTime == null)
			relatedMkInstanceTime = file.getDCT();
		if(relatedTime != null && relatedMkInstanceTime != null)
		{
			if(relatedTime.id != relatedMkInstanceTime.id)
			{
				sb.append(PipesHelper.AppendPipes(TimeMLHelper.getTimexRelation(relatedTime, relatedMkInstanceTime)));
			}else
				sb.append(PipesHelper.AppendPipes("equal"));
		}else
			sb.append(PipesHelper.AppendPipes("equal"));
				
	
		if(mkRelatedInstance.event.word != null && mkInstance.event.word != null && mkRelatedInstance.event.word.phra_id.equals(mkInstance.event.word.phra_id))
			sb.append(PipesHelper.AppendPipes("I"));
		else
			sb.append(PipesHelper.AppendPipes("O"));
		
		if(TimeMLHelper.areWordsInSameSentence(file, mkRelatedInstance.event.word, mkInstance.event.word))
			sb.append(PipesHelper.AppendPipes("I"));
		else
			sb.append(PipesHelper.AppendPipes("O"));
		
		//sb.append(PipesHelper.AppendPipes(file.get(mkInstance.event.word.sentenceNumber).toStringSyntFlat()));
		//sb.append(System.lineSeparator());
		//sb.append(PipesHelper.AppendPipes(file.get(mkRelatedInstance.event.word.sentenceNumber).toStringSyntFlat()));
		
		return sb.toString();
	}
	


	public String getExtension() {
		// TODO Auto-generated method stub
		return "e-t_relation";
	}


	@Override
	public void setValues(String values, TimeMLFile file) {
		// TODO Auto-generated method stub
		
	}



}
