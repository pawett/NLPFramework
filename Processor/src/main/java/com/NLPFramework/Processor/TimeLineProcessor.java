package com.NLPFramework.Processor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.NewsReader.Domain.EventMention;
import com.NLPFramework.RESTClient.ClientBase;
import com.NLPFramework.RESTClient.DBpediaResource;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeLinkRelationType;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.TimeType;
import com.NLPFramework.TimeML.Domain.Timex3;



public class TimeLineProcessor 
{
	private TimeMLFile file;
	
	public TimeLineProcessor(TimeMLFile file)
	{
		this.file = file;
	}
	
	public void execute()
	{
		StringBuilder sb = new StringBuilder();
		//rules: Not adjectival events, not counter-factual(with modal may, might, should... Not cognitive (think)
		//Not grammatical (PRESPART)
		//Of course, those that are counter-factual(did not happen, aka negative sentences - He did not work...
		for(TokenizedSentence sentence : file)
		{
			Hashtable<Word, EntityMapper<Annotation>> events =  sentence.annotations.get(Event.class);
			
			if(events == null)
				continue;
			
			for(Word w : events.keySet())
			{
				Event event = (Event) events.get(w).element;
				Optional<MakeInstance> mkOptional = file.getMakeInstances().stream().filter(mk -> mk.event.equals(event)).findFirst();
				if(mkOptional.isPresent())
				{
					//MakeInstance mk = mkOptional.get();
					//EventMention eventMention = (EventMention) mk.event;
					
					processEvent(event);
					
					
				/*	processEventMention(eventMention, sentence);
					if(eventMention.modality != null || !eventMention.modality.isEmpty())//Rule Not modal verbs
						continue;
					if(eventMention.factuality.equals(Factuality.COUNTERFACTUAL))
						continue;
					if(eventMention.factuality.equals(Factuality.NONFACTUAL) && eventMention.certainty.equals(Certainty.UNCERTAIN))
						continue;
					//TODO:Events that describe mental states and mental acts that involve mental or cognitive processes such as plans, love, think, know, remember, perceive, prefer, want, forget, understand, decide, decision
					
					if(mk.polarity.equals(Polarity.NEG)) //Not negated events
						continue;
					if(mk.event.word.pos.startsWith("J"))//Not adjectivals events
						continue;*/
				}
			}
			
			/*int verbPos = 0;
			for(Word v : sentence.verbs)
			{
				sb.append("Action: " + v.word);
				sb.append(System.lineSeparator());
				String subject = "";
				String cd= "";
				String when = "";
				for(Word w : sentence)
				{
					if(events.contains(w))
					{
						MakeInstance event = (MakeInstance) events.get(w).element;
						
						if(event.modality.matches(Constants.matchModal))
							continue;
						
					}
					if(w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null)
					{	
						if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A0) && w.ner.matches("PER|PERSON|ORG|ORGANIZATION"))
						   subject = subject + " " + w.word;
						if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A1) && w.ner.matches("PER|PERSON|ORG|ORGANIZATION"))
							cd = cd + " " + w.word;
						//sb.append(this.toString(w, v, w.semanticRoles.get(verbPos)));
						//sb.append(System.lineSeparator());
					}
					Timex3 timex = TimeMLHelper.getTimexFromFile(file, w);
					if(timex != null)
					{
						if(sentence.getWordDependantVerb(w).equals(v))
							when = when + " " + timex.value;
					}
				}
				sb.append("who: " + subject);
				sb.append(System.lineSeparator());
				sb.append("to whom: " + cd);
				sb.append(System.lineSeparator());
				sb.append("when: " + when);
				verbPos++;
				sb.append(System.lineSeparator());
			}*/
		}
		
		
	}
	
	private void processEvent(Event event)
	{
		TokenizedSentence sentence = file.get(event.word.sentenceNumber);
		Logger.Write("Sentence: " + sentence.toStringSyntFlat());
		Logger.Write("Action: " + event.stem);
		String subject = "";
		String cd= "";
		String when = "";
		
		Word eventDepVerb = event.word.isVerb ? event.word : sentence.getWordDependantVerb(event.word);
		
		int verbPos = sentence.verbs.indexOf(eventDepVerb);
		
		if(verbPos < 0)
		{
			Logger.Write("No verbs for " + sentence.getOriginalText());
			return;
		}
		
		TimeMLHelper.getWordSentence(sentence, event.word);
		String sentenceSRL = "";
		for(Word w : sentence)
		{
			if(w.pos.equals("WDT"))
				break;
			/*if(events.contains(w))
			{
				MakeInstance event = (MakeInstance) events.get(w).element;
				
				if(event.modality.matches(Constants.matchModal))
					continue;
				
			}*/
			
			if( w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null)
			{	
				sentenceSRL = sentenceSRL + " " + w.word;
				String coref = w.word;
				if(getMainReference(w) != null)
					coref = getMainReference(w).printCurrent();
				if(!w.ner.matches("O"))//PER|PERSON|ORG|ORGANIZATION
				{
					if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A0))
						subject =  subject + " " + w.word ;//.ner.matches("PER|PERSON|ORG|ORGANIZATION") ? subject + " " + getMainReference(w).word : subject;
					if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A1))
						cd = cd + " "+  w.word;// getMainReference(w).ner.matches("PER|PERSON|ORG|ORGANIZATION") ? cd + " " + getMainReference(w).word : cd;
					
				}else
				{
					if(!subject.isEmpty())
						subject = subject + ",";
					
					if(!cd.isEmpty())
						cd = cd + ",";
				}
				//sb.append(this.toString(w, v, w.semanticRoles.get(verbPos)));
				//sb.append(System.lineSeparator());
				
				Timex3 timex = TimeMLHelper.getTimexFromFile(file, w);
				if(timex != null)
				{
					MakeInstance mk = TimeMLHelper.getMakeInstanceFromFile(file, event.word);
					if(timex.type.equals(TimeType.DATE))
					{	
						ArrayList<TimeLink> timeLinks = TimeMLHelper.getTimeLinksForTimexAndMakeInstance(file, timex, mk);
						if(timeLinks.stream().allMatch(tl -> TimeMLHelper.getTimeLinkRelationTypeSimplified(tl.type, false).equals(TimeLinkRelationType.OVERLAP)))
						{
							if(timex.value.equals("PRESENT_REF"))
							{
								boolean hasBeenModified = false;
								timeLinks = TimeMLHelper.getTimeLinksForMakeInstance(file, mk);
								for(TimeLink tl : timeLinks)
								{
									if(tl.eventInstance != null && tl.equals(mk) && tl.relatedToEventInstance != null && TimeMLHelper.getTimeLinkRelationTypeSimplified(tl.type, false).equals(TimeLinkRelationType.OVERLAP))
									{
										ArrayList<TimeLink> relatedToRelatedEvent = TimeMLHelper.getTimeLinksForMakeInstance(file, tl.relatedToEventInstance);
										Optional<TimeLink> optTL = relatedToRelatedEvent.stream().filter(timel -> TimeMLHelper.getTimeLinkRelationTypeSimplified(timel.type, false).equals(TimeLinkRelationType.OVERLAP) && timel.relatedToTime != null).findFirst();
										if(optTL.isPresent())
										{
											when = when + " " + optTL.get().relatedToTime.value;
											hasBeenModified = true;
										}
										
										
									}
								}
								if(!hasBeenModified)
									when = when + " " + file.getDCT().value;
								
							//	TimeLink[] related = (TimeLink[]) timeLinks.stream().filter(tl -> (tl.eventInstance != null && tl.eventInstance.equals(mk) && tl.relatedToEventInstance != null) || (tl.relatedToEventInstance != null && tl.relatedToEventInstance.equals(mk) && tl.eventInstance != null)).toArray();
								//int lenght = related.length;
							}
							else								
								when = when + " " + timex.value;
						}
					}
						
				}else
				{
					MakeInstance mk = TimeMLHelper.getMakeInstanceFromFile(file, event.word);
					if(mk != null)
					{
						ArrayList<TimeLink> timeLinks = TimeMLHelper.getTimeLinksForMakeInstance(file, mk);
						
					}
					
				}
				
			}
			
		}
		
		if(when.isEmpty())
			when = when + " " + file.getDCT().value;
		
		if(!subject.isEmpty()){
		ClientBase base = new ClientBase();
		Response response = base.get(subject);
		DBpediaResource output = response.readEntity(DBpediaResource.class);
		subject = output.getResources() != null && !output.getResources().isEmpty() ? output.getResources().get(0).getSupport() : subject;
		}
		
		Logger.Write("Sentence: " + sentenceSRL);
		Logger.Write("who: " + subject);
		Logger.Write("to whom: " + cd);
		Logger.Write("when: " + when);
		Logger.Write(System.lineSeparator());
	
	}
	
	private Coreference getMainReference(Word w)
	{
		Coreference returnWord = null;
		LinkedList<Annotation> corefs = file.annotations.get(Coreference.class);
		for(Annotation annotation : corefs)
		{
			Coreference mainCoref = (Coreference) annotation;
			boolean containsWord = false;
			if(!mainCoref.word.equals(w))
			{
			
				for(Coreference c : mainCoref.coreferences)
				{
					if(c.word.equals(w))
						returnWord = mainCoref;
				}
			}else
			{
				returnWord = mainCoref;
			}
				
		}
		
		return returnWord;
	}

	private void processEventMention(EventMention eventMention, TokenizedSentence sentence) {
		// TODO Auto-generated method stub
		
	}

}
