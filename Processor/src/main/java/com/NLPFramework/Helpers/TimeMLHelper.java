package com.NLPFramework.Helpers;

import java.util.Enumeration;
import java.util.Optional;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.EventAspect;
import com.NLPFramework.TimeML.Domain.EventTense;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeLinkRelationType;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;
import com.NLPFramework.TimeML.Domain.Timex3Date;
import com.NLPFramework.TimeML.Domain.Timex3Duration;
import com.NLPFramework.TimeML.Domain.Timex3Set;
import com.NLPFramework.TimeML.Domain.Timex3Time;

public class TimeMLHelper {

	 public static TimeLinkRelationType getTimeLinkRelationTypeSimplified(TimeLinkRelationType relType, boolean relationIsReverse)
	 {
		 if(relType == null)
			 return TimeLinkRelationType.VAGUE;
		 // else
		 //return relType;
		 if(relationIsReverse)
		 {
			 switch(relType)
			 {
			 case BEFORE:
			 case IBEFORE:
				 return TimeLinkRelationType.AFTER;
			 case AFTER:
			 case IAFTER:
				 return TimeLinkRelationType.BEFORE;	
			 case BEGINS:
			 case BEGUN_BY:
				 return TimeLinkRelationType.BEFORE_OR_OVERLAP;
			 case ENDS:
			 case ENDED_BY:
				 return TimeLinkRelationType.OVERLAP_OR_AFTER;
			 case INCLUDES:
			 case IS_INCLUDED:
				 return TimeLinkRelationType.OVERLAP;	
			 case DURING:
			 case DURING_INV:
			 case SIMULTANEOUS:		 
				 return TimeLinkRelationType.OVERLAP;
			 case IDENTITY:
			 default:
				 return relType;// TimeLinkRelationType.VAGUE;
			 }
		 }else
		 {
			 switch(relType)
			 {
			 case BEFORE:
			 case IBEFORE:
				 return TimeLinkRelationType.BEFORE;
			 case AFTER:
			 case IAFTER:
				 return TimeLinkRelationType.AFTER;	
			 case BEGINS:
			 case BEGUN_BY:
				 return TimeLinkRelationType.OVERLAP_OR_AFTER;
			 case ENDS:
			 case ENDED_BY:
				 return TimeLinkRelationType.BEFORE_OR_OVERLAP;
			 case INCLUDES:
			 case IS_INCLUDED:
				 return TimeLinkRelationType.OVERLAP;	
			 case DURING:
			 case DURING_INV:
			 case SIMULTANEOUS:		 
				 return TimeLinkRelationType.OVERLAP;
			 case IDENTITY:
			 default:
				 return relType;// TimeLinkRelationType.VAGUE;
			 }
		 }


	 }
	 
	 public static Word getFirstWordOfSentence(TokenizedSentence s)
	 {
		 if(s.size() > 0)
			 return s.get(0);
		 for(TokenizedSentence subsentence : s.subSentences)
			 return getFirstWordOfSentence(subsentence);
		 
		 return null;
	 }
	 
	 
	 public static Timex3 getTimexAnnotatedByDependantVerb(TimeMLFile file, Word depVerb) 
	 {
		 
		 Timex3 relatedTimex = null;
		 for(Object tlObject : file.annotations.get(TimeLink.class))
		 {	
			 TimeLink tl = (TimeLink)tlObject;
			 Word tlDepVerb = null;
			 if(tl.relatedToEventInstance != null && tl.relatedToEventInstance.event != null && tl.relatedToEventInstance.event.word != null)
			 {
				 try{
				 TokenizedSentence s = file.get(depVerb.sentenceNumber);
				 if(s != null)
					 tlDepVerb = s.getWordDependantVerb(tl.relatedToEventInstance.event.word);
				 }catch(Exception ex)
				 {
					 int i = 0;
					 i++;
				 }
			 }
			 if(tlDepVerb != null && tlDepVerb.equals(depVerb))
			 {
				 try{
				 if(tl.type.equals(TimeLinkRelationType.OVERLAP) || tl.type.equals(TimeLinkRelationType.BEFORE_OR_OVERLAP) || tl.type.equals(TimeLinkRelationType.OVERLAP_OR_AFTER))
				 {
					 relatedTimex = tl.relatedToTime;
					 break;
				 }
				 }catch(Exception ex)
				 {
					 int i = 0;
					 i++;
				 }
			 }
		 }
		 if(relatedTimex == null)
			 relatedTimex = getTimexByDependantVerb(file, depVerb);
		 
		return relatedTimex;
	 }
	 
	
	 
	 public static TokenizedSentence getWordSentence(TimeMLFile file, Word w)
	 {
		 return file.get(w.sentenceNumber);
	 }
	 
	 public static Timex3 getTimexByDependantVerb(TimeMLFile file, Word depVerb) 
	 {
		 Timex3 time = null;
		 Timex3 contextTime = null;
		 //TokenizedSentence sentence = getWordSentence(file, depVerb);
		 if(depVerb == null)
		 {
			 Logger.WriteDebug("Error in getTimex, depVerb is null");
			 return null;
		 }
		 
		 for(TokenizedSentence sentence : file)
		 {
			 if(sentence.getFirst().sentenceNumber > depVerb.sentenceNumber)
				 break;

			 if(sentence.annotations.get(Timex3.class) != null)
			 {		
				 for(Enumeration<Word> e = sentence.annotations.get(Timex3.class).keys(); e.hasMoreElements();)
				 {
					 Word w = e.nextElement();
					 time = (Timex3) sentence.annotations.get(Timex3.class).get(w).element;
					 if(time.word != null)
					 { 
						 Word timexDepVerb = sentence.getWordDependantVerb(time.word);
						 if(timexDepVerb != null && timexDepVerb.equals(depVerb))
							 return time;
					 }
				 }
			 }
			 if(sentence.annotations.get(Timex3Date.class) != null)
			 {
				 for(Enumeration<Word> e = sentence.annotations.get(Timex3Date.class).keys(); e.hasMoreElements();)
				 {
					 Word w = e.nextElement();

					 time = (Timex3Date) sentence.annotations.get(Timex3Date.class).get(w).element;
					 if(time.word != null)
					 { Word timexDepVerb = sentence.getWordDependantVerb(time.word);
					 if(timexDepVerb != null && timexDepVerb.equals(depVerb))
						 return time;
					 }
				 }
			 }

			 if(sentence.annotations.get(Timex3Time.class) != null)
			 {
				 for(Enumeration<Word> e = sentence.annotations.get(Timex3Time.class).keys(); e.hasMoreElements();)
				 {
					 Word w = e.nextElement();
					 time = (Timex3Time) sentence.annotations.get(Timex3Time.class).get(w).element;
					 if(time.word != null)
					 { Word timexDepVerb = sentence.getWordDependantVerb(time.word);
					 if(timexDepVerb != null && timexDepVerb.equals(depVerb))
						 return time;
					 }
				 }
			 }

			 if(sentence.annotations.get(Timex3Duration.class) != null)
			 {
				 for(Enumeration<Word> e = sentence.annotations.get(Timex3Duration.class).keys(); e.hasMoreElements();)
				 {
					 Word w = e.nextElement();
					 time = (Timex3Duration) sentence.annotations.get(Timex3Duration.class).get(w).element;
					 if(time.word != null)
					 { Word timexDepVerb = sentence.getWordDependantVerb(time.word);
					 if(timexDepVerb != null && timexDepVerb.equals(depVerb))
						 return time;
					 }
				 }
			 }

			 if(sentence.annotations.get(Timex3Set.class) != null)
			 {
				 for(Enumeration<Word> e = sentence.annotations.get(Timex3Set.class).keys(); e.hasMoreElements();)
				 {
					 Word w = e.nextElement();
					 time = (Timex3Set) sentence.annotations.get(Timex3Set.class).get(w).element;
					 if(time.word != null)
					 { Word timexDepVerb = sentence.getWordDependantVerb(time.word);
					 if(timexDepVerb != null && timexDepVerb.equals(depVerb))
						 return time;
					 }
				 }
			 }
			 contextTime =  time != null && time.word != null && getSBARHead(file.get(time.word.sentenceNumber), time.word) == null ? time : contextTime;
		 }
		 
		 if(contextTime != null && contextTime.word != null)
		 {
			 TokenizedSentence sentence = file.get(contextTime.word.sentenceNumber);
			 Word timexDepVerb = sentence.getWordDependantVerb(contextTime.word);
			 if(timexDepVerb != null && !getEventTenseFromWordTense(timexDepVerb.tense).equals(getEventTenseFromWordTense(depVerb.tense)))
				 contextTime = null;
			 
			 if(contextTime != null && ((depVerb.sentenceNumber - contextTime.word.sentenceNumber) < 0 || (depVerb.sentenceNumber - contextTime.word.sentenceNumber) > 10))
				 contextTime = null;
			 
			 //if(contextTime == null)
			//	 contextTime = file.getDCT();
			 
		 }
		 
		 return file.getDCT(); //contextTime;
	 }
	 
	 public static Timex3 getTimexFromFile(TimeMLFile file, Word w)
	 {
		 Timex3 time = null;
		 for(TokenizedSentence sentence : file)
		 {

			 if(sentence.annotations.get(Timex3.class) != null)
			 {		
				 if(sentence.annotations.get(Timex3.class).keySet().contains(w))
					 return (Timex3) sentence.annotations.get(Timex3.class).get(w).element;
			 }
			 if(sentence.annotations.get(Timex3Date.class) != null)
			 {
				 if(sentence.annotations.get(Timex3Date.class).keySet().contains(w))
					 return (Timex3) sentence.annotations.get(Timex3Date.class).get(w).element;
			 }

			 if(sentence.annotations.get(Timex3Time.class) != null)
			 {
				 if(sentence.annotations.get(Timex3Time.class).keySet().contains(w))
					 return (Timex3) sentence.annotations.get(Timex3Time.class).get(w).element;
			 }

			 if(sentence.annotations.get(Timex3Duration.class) != null)
			 {
				 if(sentence.annotations.get(Timex3Duration.class).keySet().contains(w))
					 return (Timex3) sentence.annotations.get(Timex3Duration.class).get(w).element;
			 }

			 if(sentence.annotations.get(Timex3Set.class) != null)
			 {
				 if(sentence.annotations.get(Timex3Set.class).keySet().contains(w))
					 return (Timex3) sentence.annotations.get(Timex3Set.class).get(w).element;
			 }
			
		 }
		 return null;
	 }
	 
	 public static MakeInstance getMakeInstanceFromFile(TimeMLFile file, Word w)
	 {
		 if(w == null)
			 return null;
		 Optional<Annotation> mkPrevious = file.annotations.get(MakeInstance.class).stream()
				 .filter(m -> ((MakeInstance) m).event.word.equals(w))
				 .findFirst();
		 if(mkPrevious.isPresent())
		 {
			 return (MakeInstance)mkPrevious.get();
			 //previousSentenceMainEvent = ((MakeInstance)mkPrevious.get()).event.word;
		 }
		 return null;
	 }

	 public static boolean areWordsInSubSentences(TimeMLFile file, Word w , Word w2)
	 {
		 return areWordsInSameSentence(file, w, w2) && !areWordsInSameClause(file, w, w2);
	 }
	 
	 public static boolean areWordsInSameSentence(TimeMLFile file, Word w , Word w2)
	 {
		 TokenizedSentence firstWordSentence = file.get(w.sentenceNumber);
		 TokenizedSentence secondWordSentence = file.get(w2.sentenceNumber);
		 if(firstWordSentence == secondWordSentence)
			 return true;
		 
		 return false;
	 }
	 
	 public static boolean areWordsInSameClause(TimeMLFile file, Word w , Word w2)
	 {
		 TokenizedSentence firstWordSentence = file.get(w.sentenceNumber);
		 TokenizedSentence secondWordSentence = file.get(w2.sentenceNumber);
		 if(firstWordSentence != secondWordSentence)
			 return false;


		 firstWordSentence = getWordSentence(firstWordSentence, w);
		 secondWordSentence = getWordSentence(secondWordSentence, w);
		 if(firstWordSentence != secondWordSentence)
			 return false;
		 
		 return true;
	 }
	 public static TokenizedSentence getSentenceForSubsentence(TokenizedSentence s)
	 {
		 if(s.synt.equalsIgnoreCase("S") || s.synt.equalsIgnoreCase("SBAR"))
			 return s;
		 else
			 return getSentenceForSubsentence(s.prev);
	 }
	 
	 public static TokenizedSentence getWordSentence(TokenizedSentence s, Word w)
	 {
		 TokenizedSentence wordSubSentence = null;
		 if(!s.synt.equalsIgnoreCase("ROOT") && s.contains(w))
		 {
			 return getSentenceForSubsentence(s);
		 }
		 for(TokenizedSentence subsentence : s.subSentences)
		 {
			 TokenizedSentence subSentence = subsentence.getWordSubSentence(w);
			 if(subSentence != null)
				 return subSentence;
		 }
		 return wordSubSentence;
	 }

	 public static Word getSBARHead(TokenizedSentence s,Word w)
		{
			TokenizedSentence subsentence = s.getWordSubSentence(w);
			while(subsentence.prev != null)
			{
				if(subsentence.synt.equalsIgnoreCase("SBAR"))
				{
					
					return getFirstWordOfSentence(subsentence);
				}
				subsentence = subsentence.prev;
			}
			return null;
		}
	 
	 public static String getTimexRelation(Timex3 from, Timex3 to) 
	 {
		 try{
			 String[] fromValues = from.value.split("-");
			 String[] toValues = to.value.split("-");
			 if(fromValues.length >= 1 && toValues.length >= 1)
			 {
				 try{
					 int fromYear = Integer.parseInt(fromValues[0]);
					 int toYear = Integer.parseInt(toValues[0]);
					 if(fromYear > toYear)
						 return "before";
					 if(toYear > fromYear)
						 return "after";
				 }catch(Exception ex)
				 {
					 return "equal";
				 }

				 if(fromValues.length >= 2 && toValues.length >= 2)
				 {
					 try{
						 int fromMonth = Integer.parseInt(fromValues[1]);
						 int toMonth = Integer.parseInt(toValues[1]);
						 if(fromMonth > toMonth)
							 return "before";
						 if(toMonth > fromMonth)
							 return "after";
					 }catch(Exception ex)
					 {
						 return "equal";
					 }

					 if(fromValues.length >= 3 && toValues.length >= 3)
					 {
						 try
						 {
							 int fromDay = Integer.parseInt(fromValues[2]);
							 int toDay = Integer.parseInt(toValues[2]);
							 if(fromDay > toDay)
								 return "before";
							 if(toDay > fromDay)
								 return "after";
						 }catch(Exception ex)
						 {
							 return "equal";
						 }

					 }
				 }

			 }
		 }catch(Exception ex)
		 {
			 return "equal";
		 }
		 return "equal";
	 }
	 
	 public static EventAspect getEventAspectFromTense(String tense)
	 {
		 EventAspect aspect = EventAspect.NONE;
		 if(tense.contains("perfect-continuous"))
			 aspect = EventAspect.PERFECTIVE_PROGRESSIVE;
		 else if(tense.contains("perfect"))
			 aspect = EventAspect.PERFECTIVE;
		 else if(tense.contains("continuous"))
			 aspect = EventAspect.PROGRESSIVE;

		 return aspect;
	 }
		
		public static EventTense getEventTenseFromWordTense(String tense)
		{
			if(tense.startsWith("passive"))
				tense = tense.replace("passive-", "");
			if(tense.startsWith("conditional"))
				tense = tense.replace("conditional-", "");
			if(tense.startsWith("modal-have-"))
				tense = tense.replace("modal-have-", "");
			
			EventTense eventTense = EventTense.NONE;
					
			if(tense.contains("future"))
				eventTense = EventTense.FUTURE;
			
			if(tense.contains("present"))	
					eventTense = EventTense.PRESENT;
			
			
			if(tense.contains("past"))
				eventTense = EventTense.PAST; 
			
			
			if(tense.contains("gerund"))
				eventTense = EventTense.PRESPART;
								
			
			if(tense.equalsIgnoreCase("infinitive"))
				eventTense = EventTense.INFINITIVE;
						
			if(tense.contains("modal") && !tense.contains("have"))
				eventTense = EventTense.NONE;
		
			return eventTense;
		}

		public static Word getSentenceMainEvent(TokenizedSentence sentence) 
		{
			if(!sentence.synt.equalsIgnoreCase("ROOT") && sentence.verbs != null && sentence.verbs.size() > 0)
				return sentence.verbs.get(0);
			for(TokenizedSentence subSentence : sentence.subSentences)
			{
				Word verb = getSentenceMainEvent(subSentence);
				if(verb!= null)
					return verb;
			}
			
			return null;
			
		}

		public static Word getVerbModifier(TokenizedSentence verbSubSent)
		{
			for(TokenizedSentence subSent : verbSubSent.subSentences)
			{
				if(subSent != null && subSent.synt.startsWith("ADVP"))
					return subSent.subSentences.getFirst().element();
			}
			return null;
		}
		
		public static Word getVerbModifier(TimeMLFile file, Word depVerb)
		{
			TokenizedSentence s = getWordSentence(file, depVerb);
			TokenizedSentence subSentence = s.getWordSubSentence(depVerb);
			while(subSentence.prev != null && (subSentence.prev.synt.startsWith("VP") || subSentence.prev.synt.startsWith("ADVP")))
			{
				subSentence = subSentence.prev;
			}
			//now subSentence is the head of the verb clause
			
			Word verbModifier = getVerbModifier(subSentence);
			if(verbModifier != null)
				return verbModifier;
			
			for(TokenizedSentence subSent : subSentence.subSentences)
			{
				if(subSent.synt.startsWith("VP"))
					verbModifier = getVerbModifier(subSent);
				if(verbModifier != null)
					return verbModifier;
			}
			
			
			return null;		
			
		}
}
