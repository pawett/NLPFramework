package com.NLPFramework.Domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Optional;

import com.NLPFramework.TimeML.Domain.EventWithContext;
import com.NLPFramework.TimeML.Domain.Time;

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
		
		eventTimes.get(keyTime).add(event);
	}
	
	public String getEventTimesOrderedByTime()
	{
		StringBuilder sb = new StringBuilder();
		ArrayList<Time> orderedTimes = new ArrayList<Time>(eventTimes.keySet());
		Collections.sort(orderedTimes);
		
		for(Time t : orderedTimes)
		{
			StringBuilder sbEvents = new StringBuilder();
			sbEvents.append(t.toString() + "\t");
			ArrayList<EventWithContext> events = eventTimes.get(t);
			for(EventWithContext event : events)
			{
				sbEvents.append(event.toString()+"\t");
			}
			sb.append(sbEvents.toString());
			sb.append(System.lineSeparator());
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
