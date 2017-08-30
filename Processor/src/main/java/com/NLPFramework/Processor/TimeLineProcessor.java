package com.NLPFramework.Processor;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.EntityType;
import com.NLPFramework.Domain.JournalistInfo;
import com.NLPFramework.Domain.NER;
import com.NLPFramework.Domain.NERCoreference;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.NERCoreferenceHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.NewsReader.Domain.EventMention;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLine;
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
	
	public void execute(TimeLine tl)
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
					
					processEvent(event, tl);
					
				}
			}
			
		}			
		
	}

	
	private void processEvent(Event event, TimeLine tl)
	{
		TokenizedSentence sentence = file.get(event.word.sentenceNumber);
	//	Logger.Write("Sentence: " + sentence.toStringSyntFlat());
		//Logger.Write("Action: " + event.stem);
		String subject = "";
		String cd= "";
		String when = "";
		
		Word eventDepVerb = event.word.isVerb ? event.word : sentence.getWordDependantVerb(event.word);
		if(eventDepVerb == null)
			return;
		//Logger.Write("DepVerb: " + (eventDepVerb != null ? eventDepVerb.word : "None"));
		int verbPos = sentence.verbs.indexOf(eventDepVerb);
		
		if(verbPos < 0)
		{
			Logger.Write("No verbs for " + sentence.getOriginalText());
			return;
		}
		
		
		LinkedList<Word> A0t = new LinkedList<>();
		LinkedList<Word> A1t = new LinkedList<>();
		//if(sentence.semanticRoles.get(eventDepVerb) != null && sentence.semanticRoles.get(eventDepVerb).containsKey(PropBankArgument.A0))
		if(sentence.getSemanticRoleForEvent(eventDepVerb, PropBankArgument.A0) != null)
			A0t =  sentence.getSemanticRoleForEvent(eventDepVerb, PropBankArgument.A0).words;
		//if(sentence.semanticRoles.get(eventDepVerb) != null && sentence.semanticRoles.get(eventDepVerb).containsKey(PropBankArgument.A1))
		if(sentence.getSemanticRoleForEvent(eventDepVerb, PropBankArgument.A1) != null)
			A1t = sentence.getSemanticRoleForEvent(eventDepVerb, PropBankArgument.A1).words;
		
		LinkedList<Word> A0 = new LinkedList<>();
	//	Logger.Write("A0 BEFORE::" + A0t);
		ArrayList<Word> wordsToRemove = new ArrayList<>();
	
		int i = 0;
		for(Word w : A0t)
		{
			int te =0;
			if(!TimeMLHelper.isWordInNamePhrase(sentence, w) || w.synt.startsWith("SBAR") || w.pos.startsWith("WDT")
					|| w.pos.equals("POS") || w.pos.equals("WP") || w.pos.startsWith("VB") || w.pos.startsWith("RB"))
			{
				//TODO:I don't agree, but it seems to be fine to add the entity owner
				//if(w.pos.equals("POS"))
				//	A0.remove(w.prev);
				if(i >= 0 && (w.synt.startsWith("SBAR") || w.pos.startsWith("WDT") || w.pos.equals("WP") || w.pos.startsWith("VB") || w.pos.startsWith("RB")))
					break;
				
			
			}else
				A0.add(w);
			i++;
		}
		
		i = 0;
		LinkedList<Word> A1 = new LinkedList<>();
		Word sbarHead = null;
		int a = 0;
		for(Word w : A1t)
		{//TODO:Try to use if(TimeMLHelper.getSBARHead(sentecte, w) to check if the words are in a subordinate sentence regarding the first words
			int y= 0;
			if( !TimeMLHelper.isWordInNamePhrase(sentence, w) || w.synt.startsWith("SBAR") || w.pos.startsWith("WDT")
					|| w.pos.equals("POS") || w.pos.equals("WP") || w.pos.startsWith("VB"))
			{
				//if(w.pos.equals("POS"))
				//	A1.remove(w.prev);
					
				if(i >= 0 && (w.synt.contains("SBAR") || w.pos.startsWith("WDT") || w.pos.equals("WP") || w.pos.startsWith("VB")))
					break;
				
				
			}else
				A1.add(w);
			
			i++;
		}
		
		//A0.removeAll(wordsToRemove);
		
		//TokenizedSentence A0Coref = annotateCoreferences(A0);
		
		//A0 = A0Coref;
		
		//Logger.Write("A0 after Coref:" + A0);
		//TokenizedSentence A1Coref =  annotateCoreferences(A1);;
	
		//A1 = A1Coref;
		
		JournalistInfo info = new JournalistInfo();
		info.what = event;
		
		//info
		
		//annotateNERs(sentence, A0);
		LinkedList<NER> allA0NERs = new LinkedList<>();
		for(Word w : A0)
		{
			int t= 0;
			NER ner = NERCoreferenceHelper.getMainNERFromWord(file, w);
			if(ner != null)
				allA0NERs.add(ner);
				
			/*TokenizedSentence currentWordSentence = TimeMLHelper.getWordSentence(file, w);
			if(currentWordSentence.annotations.get(NER.class) != null && currentWordSentence.annotations.get(NER.class).get(w) != null)
			{
				EntityMapper<Annotation> nerAnnotation = currentWordSentence.annotations.get(NER.class).get(w);
				NER ner = (NER)nerAnnotation.element;
				if(ner.type != null && (ner.type.equals(EntityType.FINANCIAL) || ner.type.equals(EntityType.PERSON) || ner.type.equals(EntityType.ORGANIZATION) || ner.type.equals(EntityType.PRODUCT)))
				{
					info.actors.add(((NER)nerAnnotation.element));
					//Logger.Write("NER found " + ((NER)nerAnnotation.element).entityName + " for word " + w + " in " + A0.toString());
				}else
					Logger.Write("Not NER for " + w + " in " + A0.toString());
			}else
				Logger.Write("Not NER for " + w + " in " + A0.toString());*/

		}
		if(!allA0NERs.isEmpty())
			info.actors.add(allA0NERs.getLast());
		//Logger.Write("A0::" + A0);
		
		
	
		LinkedList<NER> allA1NERs = new LinkedList<>();
		for(Word w : A1)
		{
			NER ner = NERCoreferenceHelper.getMainNERFromWord(file, w);
			if(ner != null && !info.patients.contains(ner))
				allA1NERs.add(ner);
			/*TokenizedSentence currentWordSentence = TimeMLHelper.getWordSentence(file, w);
			if(currentWordSentence.annotations.get(NER.class) != null && currentWordSentence.annotations.get(NER.class).get(w) != null)
			{
				EntityMapper<Annotation> nerAnnotation = currentWordSentence.annotations.get(NER.class).get(w);
				NER ner = (NER)nerAnnotation.element;
				if(ner.type != null && (ner.type.equals(EntityType.FINANCIAL) || ner.type.equals(EntityType.PERSON) || ner.type.equals(EntityType.ORGANIZATION) || ner.type.equals(EntityType.PRODUCT)))
				{
					info.patients.add(((NER)nerAnnotation.element));
					//Logger.Write("NER found " + ((NER)nerAnnotation.element).entityName + " for word " + w + " in " + A0.toString());
				}else
					Logger.Write("Not NER for " + w + " in " + A1.toString());
			}else
				Logger.Write("Not NER for " + w + " in " + A1.toString());*/

		}
		
		if(!allA1NERs.isEmpty())
			info.patients.add(allA1NERs.getLast());
		
		/*if(sentence.annotations.get(NER.class) != null && sentence.annotations.get(NER.class).keySet() != null){
			Logger.Write("NER for sentence " + sentence.originalText);
			for(Word w : sentence.annotations.get(NER.class).keySet())
			{
				EntityMapper<Annotation> annotation = sentence.annotations.get(NER.class).get(w);
				Logger.Write(((NER)annotation.element).entityName);
			}
		}
		*/
	//	Logger.Write("A1::" + A1);
		
	
		ArrayList<Annotation> timexes = TimeMLHelper.getSentenceTimexes(sentence);
		Annotation relatedTimexAnnotation = null;
		
		MakeInstance eventMakeInstance = TimeMLHelper.getMakeInstanceFromFile(file, event.word);
		
		Timex3 relatedTimex = processEventRelatedTimexes(sentence, eventDepVerb, timexes, relatedTimexAnnotation, eventMakeInstance);
		int y = 2;
		Timex3 timexByEvent = processRelatedEventMakeInstancesTimeLinks(eventMakeInstance);
		
		ArrayList<TimeLink> t0TimeLinks = TimeMLHelper.getAllT0RelatedTimeLinks(file);
		
		if(relatedTimex != null && timexByEvent != null)
		{
			Logger.WriteDebug("We have two winners!! :: " + relatedTimex.value + " : " + timexByEvent.value);
		}
		
		info.when = relatedTimex != null ? relatedTimex : timexByEvent;
		
		
		
			Logger.Write("Sentence: " + sentence.originalText);
			Logger.Write("What: " + event.stem);
			for(NER n : info.actors)
			{
				Logger.Write("who: " + n.entityName );
			}
			for(NER n : info.patients)
			{
				Logger.Write("to whom: " + n.entityName );
			}
			
			if(info.when != null)
			Logger.Write("when: " + info.when.value);
			Logger.Write(System.lineSeparator());
			tl.AddEvent(info, file.getDocID());
			
		
	
	}

	private Timex3 processRelatedEventMakeInstancesTimeLinks(MakeInstance eventMakeInstance) 
	{
		ArrayList<TimeLink> relatedTimeLinks = TimeMLHelper.getTimeLinksForMakeInstance(file, eventMakeInstance);
		
		for(TimeLink relatedTimeLink : relatedTimeLinks)
		{
			//only for tlinks that relate MakeInstances, so we can navigate through them
			if(relatedTimeLink.eventInstance != null && relatedTimeLink.relatedToEventInstance != null)
			{
				MakeInstance relatedMakeInstance = relatedTimeLink.eventInstance.equals(eventMakeInstance) ? relatedTimeLink.relatedToEventInstance : relatedTimeLink.eventInstance;
				if(timeLinkIsOfTypeOverlap(relatedTimeLink))
				{
					ArrayList<TimeLink> relatedMakeInstanceTimeLinks = TimeMLHelper.getTimeLinksForMakeInstance(file, relatedMakeInstance);
					List<TimeLink> relatedMakeInstanceOverlapTimeLinks =  relatedMakeInstanceTimeLinks.stream().filter(rmtl -> rmtl.relatedToTime != null && timeLinkIsOfTypeOverlap(rmtl)).collect(Collectors.toList());
					
					for(TimeLink relatedMakeInstanceOverlapTimeLink : relatedMakeInstanceOverlapTimeLinks)
					{
						if(relatedMakeInstanceOverlapTimeLink.type.toString().matches("DATE|TIME"))
						{
							return relatedMakeInstanceOverlapTimeLink.relatedToTime;
						}
						Logger.Write(relatedMakeInstanceOverlapTimeLink.relatedToTime.value);
					}
				}
			}	
		}
		return null;
	}
		
	

	private boolean timeLinkIsOfTypeOverlap(TimeLink relatedTimeLink) {
		return TimeMLHelper.getTimeLinkRelationTypeSimplified(relatedTimeLink.type, false).equals(TimeLinkRelationType.OVERLAP);
	}

	private Timex3 processEventRelatedTimexes(TokenizedSentence sentence, Word eventDepVerb, 
			ArrayList<Annotation> timexes, Annotation relatedTimex, MakeInstance eventMakeInstance) 
	{
		Optional<Annotation> annOpt = timexes.stream().filter(t -> sentence.getWordDependantVerb(((Timex3) t).word) != null && sentence.getWordDependantVerb(((Timex3) t).word).equals(eventDepVerb)).findFirst();
		
		if(annOpt.isPresent())
			relatedTimex = annOpt.get();
		
		if(relatedTimex != null)
		{
			Timex3 timex = ((Timex3)relatedTimex);
			
			
			if(timex.type.equals(TimeType.DATE) || timex.type.equals(TimeType.TIME))
			{	
				if(!timex.value.equals("PAST_REF") && !timex.value.equals("FUTURE_REF"))
				{	
					ArrayList<TimeLink> timeLinks = TimeMLHelper.getTimeLinksForTimexAndMakeInstance(file, timex, eventMakeInstance);
					if(timeLinks.stream().allMatch(tlink -> timeLinkIsOfTypeOverlap(tlink)))
					{
						return timex.value.equals("PRESENT_REF") ? file.getDCT() : timex;
					}else
					{
						Logger.WriteDebug("ERROR");
					}
				}
			}else
			{
				Logger.WriteDebug("Type : " + timex.type + " Value: "+ timex.value);
			}
		}
		
		//Not related times in sentence, chek the relation to t0
		
		ArrayList<TimeLink> timeLinksT0 = TimeMLHelper.getTimeLinksForMakeInstance(file, eventMakeInstance);
		if(timeLinksT0 != null && !timeLinksT0.isEmpty())
		{
			List<TimeLink> tlt0List = timeLinksT0.stream().filter(tlt0 -> tlt0.relatedToTime != null && tlt0.relatedToTime.id.equals("0")).collect(Collectors.toList());
			
			if(tlt0List != null && !tlt0List.isEmpty() && timeLinkIsOfTypeOverlap(tlt0List.get(0)))
				return tlt0List.get(0).relatedToTime;

			
		}
		
		return null;
	}

	

	private void processEventMention(EventMention eventMention, TokenizedSentence sentence) {
		// TODO Auto-generated method stub
		
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
	
	//	Logger.Write("A0::" + A0);
	//	Logger.Write("A1::" + A1);
		

		
	
	/*	if(sentence.annotations.get(JournalistInfo.class) != null)
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
		}*/
	/*	
		
		
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
			
		}*/
		
	/*	if(when.isEmpty())
			when = when + " dct: " + file.getDCT().value;
		*/
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

}
