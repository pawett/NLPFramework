package com.NLPFramework.Processor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.Entity;
import com.NLPFramework.Domain.JournalistInfo;
import com.NLPFramework.Domain.NER;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.NewsReader.Domain.EventMention;
import com.NLPFramework.RESTClient.ClientBase;
import com.NLPFramework.RESTClient.DBpediaResource;
import com.NLPFramework.RESTClient.Resource;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeLinkRelationType;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.TimeType;
import com.NLPFramework.TimeML.Domain.Timex3;
import com.NLPFramework.externalTools.StanfordSynt;



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
					
				}
			}
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
		
		Word eventDepVerb = event.word.isVerb ? event.word : null;//only verbs sentence.getWordDependantVerb(event.word);
		if(eventDepVerb == null)
			return;
		Logger.Write("DepVerb: " + (eventDepVerb != null ? eventDepVerb.word : "None"));
		int verbPos = sentence.verbs.indexOf(eventDepVerb);
		
		if(verbPos < 0)
		{
			Logger.Write("No verbs for " + sentence.getOriginalText());
			return;
		}
		
		TokenizedSentence A0 = new TokenizedSentence();
		TokenizedSentence A1 = new TokenizedSentence();
		if(sentence.semanticRoles.get(eventDepVerb) != null && sentence.semanticRoles.get(eventDepVerb).containsKey(PropBankArgument.A0))
			A0 = sentence.semanticRoles.get(eventDepVerb).get(PropBankArgument.A0).words;
		if(sentence.semanticRoles.get(eventDepVerb) != null && sentence.semanticRoles.get(eventDepVerb).containsKey(PropBankArgument.A1))
			A1 = sentence.semanticRoles.get(eventDepVerb).get(PropBankArgument.A1).words;
		
		
		Logger.Write("A0 BEFORE::" + A0);
		for(Word w : A0)
		{
			if(!TimeMLHelper.areWordsInSameClause(file, w, eventDepVerb))
				A0.remove(w);
		}
		
		Logger.Write("A0::" + A0);
		
		
		
		Logger.Write("A1 BEFORE::" + A1);
		for(Word w : A1)
		{
			if(!TimeMLHelper.areWordsInSameClause(file, w, eventDepVerb))
				A1.remove(w);
		}
		
		Logger.Write("A1::" + A1);
		
	
		
		for(Word w : A0)
		{
			if(getMainReference(w) != null)
			{
				Word coreferenceWord = getMainReference(w).word;
				//A0 = new TokenizedSentence();
				//A0.add(coreferenceWord);
				
				/*for(int i = 1 ; i< getMainReference(w).offset && coreferenceWord != null; i++)
				{
					coreferenceWord = coreferenceWord.next;
					A0.add(coreferenceWord);
				}*/
				Logger.Write("Coreference for " + w.word + " :: " + getMainReference(w).toString());
			}
		}
		
		for(Word w : A1)
		{
			if(getMainReference(w) != null)
			{
				Word coreferenceWord = getMainReference(w).word;
				A1 = new TokenizedSentence();
				A1.add(coreferenceWord);
				
				for(int i = 1 ; i< getMainReference(w).offset && coreferenceWord != null; i++)
				{
					coreferenceWord = coreferenceWord.next;
					A1.add(coreferenceWord);
				}
				Logger.Write("Coreference for " + w.word + " :: " + getMainReference(w).toString());
			}
		}
/*
		for(Word w : sentence)
		{		
			if(!TimeMLHelper.areWordsInSameClause(file, w, eventDepVerb))
				continue;
			if( w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null
					)//&& w.ner.matches("PER|PERSON|ORG|ORGANIZATION"))
			{	

				if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A0))
				{
					//if(getMainReference(w) != null)
					A0.add(w);
				}
				if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A1))
					A1.add(w);
			}
		}
		
		/*for(Word w : A0)
		{
			if(getMainReference(w) != null)
			{
				Word coreferenceWord = getMainReference(w).word;
				A0 = new TokenizedSentence();
				A0.add(coreferenceWord);
				
				for(int i = 1 ; i< getMainReference(w).offset && coreferenceWord != null; i++)
				{
					coreferenceWord = coreferenceWord.next;
					A0.add(coreferenceWord);
				}
				Logger.Write("Coreference for " + w.word + " :: " + getMainReference(w).toString());
			}
		}
		
		for(Word w : A1)
		{
			if(getMainReference(w) != null)
			{
				Word coreferenceWord = getMainReference(w).word;
				A1 = new TokenizedSentence();
				A1.add(coreferenceWord);
				
				for(int i = 1 ; i< getMainReference(w).offset && coreferenceWord != null; i++)
				{
					coreferenceWord = coreferenceWord.next;
					A1.add(coreferenceWord);
				}
				Logger.Write("Coreference for " + w.word + " :: " + getMainReference(w).toString());
			}
		}*/
	
		Logger.Write("A0::" + A0);
		Logger.Write("A1::" + A1);
		
		TimeMLHelper.getWordSentence(sentence, event.word);
		String sentenceSRL = "";
	/*	ClientBase base = new ClientBase();
		Response response = base.get(file.getOriginalText());
		//String returnString = response.readEntity(String.class);
		DBpediaResource output = response.readEntity(DBpediaResource.class);
		
		Logger.Write("Sentence: " + sentence.originalText);
		for(Resource r : output.getResources())
		{
			//Word w =sentence.get(Integer.parseInt(r.getOffset()));
			Logger.Write("NER detected: "+ r.getURI() + " Word: " +r.getOffset() + " " + r.getTypes());
		}
		
		*/
		if(sentence.annotations.get(JournalistInfo.class) != null)
		{
			EntityMapper<Annotation> jiMap = sentence.annotations.get(JournalistInfo.class).get(eventDepVerb);
			if(jiMap != null)
			{
				JournalistInfo ji = (JournalistInfo) jiMap.element;
				Logger.Write("Event: " + ji.what.word.word);
				if(ji.actors.size() > 0)
				{
					Logger.Write("Authors:");
					for(Entity e : ji.actors)
						Logger.Write(e.toString());
				}
				
				if(ji.patients.size() > 0)
				{
					Logger.Write("Patients:");
					for(Entity e : ji.patients)
						Logger.Write(e.toString());
				}
				
			}
		}
		
		for(Word w : sentence)
		{
			if(w.pos.equals("WDT"))
				break;
			
			
			if( w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null)
			{	
				sentenceSRL = sentenceSRL + " " + w.word;
				String coref = w.word;
				if(getMainReference(w) != null)
					coref = getMainReference(w).printCurrent();
				if(!w.ner.matches("PER|PERSON|ORG|ORGANIZATION"))//PER|PERSON|ORG|ORGANIZATION
				{
					if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A0))
						subject =   subject + " " + coref;
					if(w.semanticRoles.get(verbPos).argument.equals(PropBankArgument.A1))
						cd = cd + " "+ coref;
					
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
			when = when + " dct: " + file.getDCT().value;
		
		/*if(!subject.isEmpty())
		{
			ClientBase base = new ClientBase();
			Response response = base.get(sentence.toString());
			String resp = response.readEntity(String.class);
			DBpediaResource output = response.readEntity(DBpediaResource.class);
			if(output.getResources() != null)
			{
				Logger.WriteDebug("Resources for " + sentence.toString());
 				for(Resource r : output.getResources())
				{
					
					Logger.WriteDebug(r.getSurfaceForm());
				}
			}
			subject = output.getResources() != null && !output.getResources().isEmpty() ? output.getResources().get(0).getSupport() : subject;
			
		}*/
		
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
		if(corefs == null)
			return null;
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
