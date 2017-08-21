package com.NLPFramework.Formatters.TimeML;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

public class EventDCTRelationFormatter  implements IFileFormatter 
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
		
		if(mkInstance == null || timex == null || !(timex.id.equals("0") || timex.id.equals("t0")))
			return null;
		
		TokenizedSentence sentence = file.get(mkInstance.event.word.sentenceNumber);
		
		sb.append(file.getName());
		sb.append(PipesHelper.AppendPipes(tl.id));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.eventClass));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.lemma));
		sb.append(PipesHelper.AppendPipes(mkInstance.event.word.pos));
		
		Word depVerb = null;
		Timex3 relatedTime = null;
		if(mkInstance.event.word != null)
			depVerb = sentence.getWordDependantVerb(mkInstance.event.word);
		if(depVerb != null)// && mkInstance.event.word.depverb != null)
		{
			if(!mkInstance.event.word.isVerb)
			{	//sb.append(PipesHelper.AppendPipes(depVerb.tense));
				sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventTenseFromWordTense(depVerb.tense)));
				sb.append(PipesHelper.AppendPipes(TimeMLHelper.getEventAspectFromTense(depVerb.tense)));
				sb.append(PipesHelper.AppendPipes(mkInstance.modality != null ? mkInstance.modality.trim().toLowerCase() : null));
				Word verbModifier = TimeMLHelper.getVerbModifier(file, depVerb);
				if(verbModifier != null)
				sb.append(PipesHelper.AppendPipes(verbModifier.lemma));
				else
					sb.append(PipesHelper.AppendPipes("-"));
					
			}else
			{
				sb.append(PipesHelper.AppendPipes(mkInstance.tense));
				sb.append(PipesHelper.AppendPipes(mkInstance.aspect));
				sb.append(PipesHelper.AppendPipes(mkInstance.modality != null ? mkInstance.modality.trim().toLowerCase() : null));
				sb.append(PipesHelper.AppendPipes("-"));	
			}
			
			relatedTime = TimeMLHelper.getTimexByDependantVerb(file, depVerb);
			if(relatedTime != null)
			{
				if(relatedTime.id != timex.id)
				{
					sb.append(PipesHelper.AppendPipes(TimeMLHelper.getTimexRelation(relatedTime, file.getDCT())));
				}else
					sb.append(PipesHelper.AppendPipes("equal"));
			}else
				sb.append(PipesHelper.AppendPipes("equal"));
		}
		else
		{
			sb.append(PipesHelper.AppendPipes(mkInstance.tense));
			sb.append(PipesHelper.AppendPipes(mkInstance.aspect));
			sb.append(PipesHelper.AppendPipes(mkInstance.modality != null ? mkInstance.modality.trim().toLowerCase() : null));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
		}
		
		//String a = PipesHelper.AppendPipes(mkInstance.modality != null ? mkInstance.modality.trim() : null);
		//Logger.WriteDebug(a);
		
				
		if(mkInstance.event.word != null && sentence.getWordPP(mkInstance.event.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(mkInstance.event.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		if(relatedTime != null)
			sb.append(PipesHelper.AppendPipes(relatedTime.type.toString()));
		else
			sb.append(PipesHelper.AppendPipes(timex.type.toString()));
			
		depVerb = null;
		if(relatedTime != null && relatedTime.word != null)
			depVerb = file.get(relatedTime.word.sentenceNumber).getWordDependantVerb(relatedTime.word);
		if(depVerb != null)
		{
			MakeInstance timexMakeInstance = file.getMakeInstanceByVerb(depVerb);
			if(timexMakeInstance != null && !timexMakeInstance.equals(mkInstance))
			{
				sb.append(PipesHelper.AppendPipes(timexMakeInstance.tense));
				sb.append(PipesHelper.AppendPipes(timexMakeInstance.aspect));
				sb.append(PipesHelper.AppendPipes(timexMakeInstance.event.word.lemma));
				sb.append(PipesHelper.AppendPipes(timexMakeInstance.modality));
				Word verbModifier = TimeMLHelper.getVerbModifier(file, depVerb);
				if(verbModifier != null)
				sb.append(PipesHelper.AppendPipes(verbModifier.lemma));
				else
					sb.append(PipesHelper.AppendPipes("-"));
			}else
			{
				sb.append(PipesHelper.AppendPipes("-"));
				sb.append(PipesHelper.AppendPipes("-"));
				sb.append(PipesHelper.AppendPipes("-"));
				sb.append(PipesHelper.AppendPipes("-"));
				sb.append(PipesHelper.AppendPipes("-"));
			}
			
		}
		else
		{
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
		}
		
		if(relatedTime != null && relatedTime.word != null && sentence.getWordPP(relatedTime.word) != null)
			sb.append(PipesHelper.AppendPipes(sentence.getWordPP(relatedTime.word)));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		String syntRelation = "-";
		if(mkInstance.event.word != null && relatedTime != null && relatedTime.word != null && mkInstance.event.word.sentence.equals(relatedTime.word.sentence) && mkInstance.event.word.phra_id.equals(relatedTime.word.phra_id))
			syntRelation = "sub-sent";
		
		if(mkInstance.event.word != null && relatedTime != null && relatedTime.word != null && mkInstance.event.word.sentence.equals(relatedTime.word.sentence) && mkInstance.event.word.mainphraseIOB.equals(relatedTime.word.mainphraseIOB))
			syntRelation = "phrase";
		sb.append(PipesHelper.AppendPipes(syntRelation));
		Word SBAREventHead = TimeMLHelper.getSBARHead(file.get(mkInstance.event.word.sentenceNumber),mkInstance.event.word);
		if(SBAREventHead != null)
			sb.append(PipesHelper.AppendPipes(SBAREventHead.lemma));
		else
			sb.append(PipesHelper.AppendPipes("-"));
		
		if(relatedTime != null && relatedTime.word != null)
		{
			Word SBARelatedTimesHead = TimeMLHelper.getSBARHead(file.get(relatedTime.word.sentenceNumber), relatedTime.word);
			if(SBARelatedTimesHead != null)
				sb.append(PipesHelper.AppendPipes(SBARelatedTimesHead.lemma));
			else
				sb.append(PipesHelper.AppendPipes("-"));
		}else
			sb.append(PipesHelper.AppendPipes("-"));
		
		
		//sb.append(PipesHelper.AppendPipes(s.toStringSyntFlat()));
		return sb.toString();
	}
	


	public String getExtension() {
		// TODO Auto-generated method stub
		return "e-dct_relation";
	}


	@Override
	public void setValues(String values, TimeMLFile file) {
		// TODO Auto-generated method stub
		
	}



}
