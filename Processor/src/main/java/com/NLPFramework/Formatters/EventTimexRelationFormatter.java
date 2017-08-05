package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.EventAspect;
import com.NLPFramework.TimeML.Domain.EventTense;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

public class EventTimexRelationFormatter  implements IFileFormatter 
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
				
		MakeInstance mkInstance = tl.relatedToEventInstance != null ? tl.relatedToEventInstance : tl.eventInstance;
		Timex3 timex = tl.relatedToTime;
		
		if(mkInstance == null || timex == null || (timex.id.equals("0") || timex.id.equals("t0")))
			return null;

		
		if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber != (timex.word.sentenceNumber))
			return null;
		
		String syntRelation = "sentence";
		if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber == (timex.word.sentenceNumber) && mkInstance.event.word.phra_id.equals(timex.word.phra_id))
			syntRelation = "sub-sent";
		
		if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber == (timex.word.sentenceNumber) && mkInstance.event.word.mainphraseIOB.equals(timex.word.mainphraseIOB))
			syntRelation = "phrase";
		//The filter must be: if event is main verb and timex in a subordinate sentence or clause
		// and both event and timex are in the same clause
		
		//if(syntRelation.equals("sentence"))
		//	return null;
		
		TokenizedSentence sentence = file.get(mkInstance.event.word.sentenceNumber);
		Word eventDepVerb = sentence.getWordDependantVerb(mkInstance.event.word);
		Word timexDepVerb = sentence.getWordDependantVerb(timex.word);
	
		Word sentenceMainVerb = TimeMLHelper.getSentenceMainEvent(sentence);	
		
		//if(sentenceMainVerb != null && eventDepVerb != null && !sentenceMainVerb.equals(eventDepVerb) && !TimeMLHelper.areWordsInSameClause(file, mkInstance.event.word, timex.word))
	//		return null;
		
		sb.append(file.getName());
		sb.append(PipesHelper.AppendPipes(tl.id));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.eventClass));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.lemma));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.pos));
		Word depVerb = null;
		if(mkInstance.event.word != null)
			depVerb = file.get(mkInstance.event.word.sentenceNumber).getWordDependantVerb(mkInstance.event.word);
		if(depVerb != null)// && mkInstance.event.word.depverb != null)
		{
			if(!mkInstance.event.word.isVerb)
			{	//sb.append(PipesHelper.AppendPipes(depVerb.tense));
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventTenseFromWordTense(depVerb.tense)));
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventAspectFromTense(mkInstance.event.word.depverb.tense)));
			}else
			{
				sb.append(PipesHelper.AppendPipes(mkInstance.tense));
				sb.append(PipesHelper.AppendPipes(mkInstance.aspect));
			}
			Timex3 relatedTime = TimeMLHelper.getTimexByDependantVerb(file, depVerb);
			if(relatedTime != null)
			{
				if(relatedTime.id != timex.id)
				{
					sb.append(PipesHelper.AppendPipes(TimeMLHelper.getTimexRelation(relatedTime, timex)));
				}else
					sb.append(PipesHelper.AppendPipes("equal"));
			}else
				//sb.append(PipesHelper.AppendPipes(TimeMLHelper.getTimexRelation(file.getDCT(), timex)));
				sb.append(PipesHelper.AppendPipes("-"));
		}
		else
		{
			sb.append(PipesHelper.AppendPipes(mkInstance.event.tense));
			sb.append(PipesHelper.AppendPipes(mkInstance.event.aspect));
			sb.append(PipesHelper.AppendPipes("equal"));
		}
		
				
		if(mkInstance.event.word != null && sentence.getWordPP(mkInstance.event.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(mkInstance.event.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		sb.append(PipesHelper.AppendPipes(timex.type.toString()));
			
		depVerb = null;
		if(timex.word != null)
			depVerb = file.get( timex.word.sentenceNumber).getWordDependantVerb(timex.word);
		if(depVerb != null)
		{
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventTenseFromWordTense(depVerb.tense)));
			sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventAspectFromTense(depVerb.tense)));
			sb.append(PipesHelper.AppendPipes(timex.word.lemma));
		}
		else
		{
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
		}
		
		if(timex.word != null && sentence.getWordPP(timex.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(timex.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		
		sb.append(PipesHelper.AppendPipes(syntRelation));	
		
		Word SBAREventHead = TimeMLHelper.getSBARHead(file.get(mkInstance.event.word.sentenceNumber),mkInstance.event.word);
		//if(SBAREventHead != null)
		//	sb.append(PipesHelper.AppendPipes(SBAREventHead.lemma));
		//else
			sb.append(PipesHelper.AppendPipes("-"));
		
		Word SBARTimexHead = TimeMLHelper.getSBARHead(file.get(timex.word.sentenceNumber), timex.word);
		//if(SBARTimexHead != null)
		//	sb.append(PipesHelper.AppendPipes(SBARTimexHead.lemma));
		//else
			sb.append(PipesHelper.AppendPipes("-"));
		TokenizedSentence s = file.get(mkInstance.event.word.sentenceNumber);
		FeaturesTimexAnnotatedFormatter formatter = new FeaturesTimexAnnotatedFormatter();
		
		//sb.append(PipesHelper.AppendPipes(s.toStringSyntFlat()));
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
