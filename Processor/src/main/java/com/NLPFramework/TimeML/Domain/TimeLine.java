package com.NLPFramework.TimeML.Domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.OptionalInt;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Entity;

public class TimeLine
{
	private Hashtable<String, ArrayList<Event>> dateEvents = new Hashtable<>();
	
	private ArrayList<EventWithContext> events = new ArrayList<>();
	private ArrayList<Time> times = new ArrayList<>();
	private ArrayList<Entity> entities = new ArrayList<>();
	
	private Hashtable<Time,Hashtable<Entity,ArrayList<EventWithContext>>> eventTimes = new Hashtable<>();
	
	
	public void AddEvent(String date, EventWithContext event, Entity entity)
	{
		String[] dateValues = date.split("-");
		Time time = new Time();
		try
		{
			time.year = OptionalInt.of(Integer.parseInt(dateValues[0]));
			time.month = OptionalInt.of(Integer.parseInt(dateValues[1]));
			time.day = OptionalInt.of(Integer.parseInt(dateValues[2]));
		}catch(Exception ex)
		{
			Logger.WriteDebug("Date without all values");
		}
		
		if(!eventTimes.containsKey(time))
			eventTimes.put(time, new Hashtable<>());
		
		if(!eventTimes.get(time).containsKey(entity))
		{
			eventTimes.get(time).put(entity, new ArrayList<>());
		}
		
		eventTimes.get(time).get(entity).add(event);
		//dateEvents.put(date, event);
	}
}
