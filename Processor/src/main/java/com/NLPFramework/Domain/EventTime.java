package com.NLPFramework.Domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Processor.Configuration;
import com.NLPFramework.TimeML.Domain.EventWithContext;
import com.NLPFramework.TimeML.Domain.Time;
import com.NLPFramework.externalTools.WNInterface;

public class EventTime 
{

	private Hashtable<Time,ArrayList<EventWithContext>> eventTimes = new Hashtable<>();
	
	public void addEventTime(Time time, EventWithContext event)
	{
		ArrayList<Time> times = new ArrayList<>(eventTimes.keySet());
		Optional<Time> optionalKeyTime = times.stream().filter(t -> t.equals(time)).findFirst();
		Time keyTime = time;
		if(!optionalKeyTime.isPresent())
		{
			eventTimes.put(time, new ArrayList<>());
			keyTime = time;
		}else
			keyTime = optionalKeyTime.get();
		
		if(!eventTimes.get(keyTime).stream().anyMatch(e -> e.word.equals(event.word)))
			eventTimes.get(keyTime).add(event);
		else
			Logger.WriteDebug("Event duplicated for time " + time.toString() + " Event: " + event.toString());
	}
	
	public String getEventTimesOrderedByTime()
	{
		StringBuilder sb = new StringBuilder();
		//ArrayList<String> values = new ArrayList<>();
		ArrayList<Time> orderedTimes = new ArrayList<Time>(eventTimes.keySet());
		Collections.sort(orderedTimes);
		int i = 0;
		Time lastTime = null;
		for(Time t : orderedTimes)
		{
			StringBuilder sbEvents = new StringBuilder();
			
			//sbEvents.append(t.toString() + "\t");
			ArrayList<EventWithContext> events = eventTimes.get(t);
			
			Hashtable<EventWithContext, ArrayList<EventWithContext>> eventsGroupBySynonims = new Hashtable<>();
			WNInterface wn = new WNInterface(Configuration.getLanguage());
			for(EventWithContext event : events)
			{
				Optional<EventWithContext> synEvent =  eventsGroupBySynonims.keySet().stream().filter(e -> wn.isWordSynonimOf(e.word, event.word)).findFirst();
				if(synEvent.isPresent())
				{
					ArrayList<EventWithContext> synEvents = eventsGroupBySynonims.get(synEvent.get());
					if(synEvents == null)
						eventsGroupBySynonims.put(synEvent.get(), new ArrayList<>());
					eventsGroupBySynonims.get(synEvent.get()).add(event);
				}else
				{
					eventsGroupBySynonims.put(event, new ArrayList<>());
					eventsGroupBySynonims.get(event).add(event);
				}
				
			}
			
			for(EventWithContext event : eventsGroupBySynonims.keySet())
			{
				
				if(t.hasNoData())
					sbEvents.append("0" + "\t");
				else
				{
					
					if(lastTime == null || !t.toString().equals(lastTime.toString()))
					{
						i++;
						lastTime = t;
					}
					
					sbEvents.append(i + "\t");
				}
				int u = 0;
				for(EventWithContext synEvent : eventsGroupBySynonims.get(event))
				{
					if(synEvent.equals(event))
						sbEvents.append(t.toString()+"\t");
					sbEvents.append(synEvent.toString()+"\t");
					
					//values.add(t.toString()+"\t" + event.toString());
				}
				sbEvents.append(System.lineSeparator());
				//sbEvents.append(t.toString()+"\t" + event.toString()+"\t");
				//sbEvents.append(System.lineSeparator());
				//values.add(t.toString()+"\t" + event.toString());
			}
			sb.append(sbEvents.toString());
			//sb.append(System.lineSeparator());
		}
			
		return sb.toString();
	}
	
	

	public int getTotalEvents() {
		// TODO Auto-generated method stub
		int totalEvents = 0;
		for(Time t : eventTimes.keySet())
		{
			totalEvents = totalEvents + eventTimes.get(t).size();
		}
		return totalEvents;
	}
}
