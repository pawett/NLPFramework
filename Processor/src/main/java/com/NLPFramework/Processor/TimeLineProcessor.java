package com.NLPFramework.Processor;

import java.util.Hashtable;
import java.util.Optional;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.Constants;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.NewsReader.Domain.Certainty;
import com.NLPFramework.NewsReader.Domain.EventMention;
import com.NLPFramework.NewsReader.Domain.Factuality;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.Polarity;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
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
			
			for(Word w : events.keySet())
			{
				Event event = (Event) events.get(w).element;
				Optional<MakeInstance> mkOptional = file.getMakeInstances().stream().filter(mk -> mk.event.equals(event)).findFirst();
				if(mkOptional.isPresent())
				{
					MakeInstance mk = mkOptional.get();
					EventMention eventMention = (EventMention) mk.event;
					processEventMention(eventMention, sentence);
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
						continue;
				}
			}
			
			int verbPos = 0;
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
			}
		}
		
		
	}

	private void processEventMention(EventMention eventMention, TokenizedSentence sentence) {
		// TODO Auto-generated method stub
		
	}

}
