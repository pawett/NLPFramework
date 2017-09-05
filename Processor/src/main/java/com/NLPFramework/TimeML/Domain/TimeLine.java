package com.NLPFramework.TimeML.Domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import org.jgrapht.UndirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.EventTime;
import com.NLPFramework.Domain.JournalistInfo;
import com.NLPFramework.Domain.NER;

public class TimeLine
{
	private Hashtable<String, ArrayList<Event>> dateEvents = new Hashtable<>();
	
	private ArrayList<EventWithContext> events = new ArrayList<>();
	private ArrayList<Time> times = new ArrayList<>();
	private ArrayList<NER> entities = new ArrayList<>();
	private HashMap<String, EventTime> entityMap = new HashMap<>();
	
	private UndirectedGraph<String, DefaultEdge> graph =
            new SimpleGraph<String, DefaultEdge>(DefaultEdge.class);
	
	
	public HashMap<String, EventTime> getEntityMap()
	{
		return entityMap;
	}

	public ArrayList<NER> getEntities()
	{
		return entities;
	}
	
	public ArrayList<EventWithContext> getEvents()
	{
		return events;
	}
	
	public UndirectedGraph<String, DefaultEdge> getGraph()
	{
		return graph;
	}
	
	public void AddEvent(JournalistInfo ji, String fileId)
	{
		EventWithContext contextEvent = new EventWithContext();
		contextEvent.stem = ji.what.word.word;
		contextEvent.documentId = fileId;
		contextEvent.sentenceNumber = ji.what.word.sentenceNumber;
		contextEvent.word = ji.what.word;
		
		if(events.stream().anyMatch(e -> e.word.equals(contextEvent.word)))
		{
			Logger.WriteDebug("ERROR!!");
		}
		
		events.add(contextEvent);
		Time date = new Time(ji.when);
		
		//if(ji.when != null)
		//	date = getDateFromTimex(ji.when);
		graph.addVertex(contextEvent.toString());
		graph.addVertex(date.toString());
		graph.addEdge(contextEvent.toString(), date.toString());
		
		for(NER ner : ji.actors)
		{
			graph.addVertex(ner.entityName);
			graph.addEdge(contextEvent.toString(), ner.entityName);
			if(!entities.stream().anyMatch(e -> e.entityName.equals(ner.entityName)))
				entities.add(ner);
			
			if(!entityMap.containsKey(ner.entityName))
				entityMap.put(ner.entityName, new EventTime());
			entityMap.get(ner.entityName).addEventTime(date, contextEvent);			
		}
		
		for(NER ner : ji.patients)
		{
			graph.addVertex(ner.entityName);
			graph.addEdge(contextEvent.toString(), ner.entityName);
			if(!entities.stream().anyMatch(e -> e.entityName.equals(ner.entityName)))
				entities.add(ner);
			
			if(!entityMap.containsKey(ner.entityName))
				entityMap.put(ner.entityName, new EventTime());

			entityMap.get(ner.entityName).addEventTime(date, contextEvent);
		}
			
	}
	
	

	private String getDateFromTimex(Timex3 when) {
		String[] dateValues = when.value.split("-");
		StringBuilder sb = new StringBuilder();
		if(dateValues.length > 0)
		{
			sb.append(dateValues[0]);
			if(dateValues.length > 1)
			{
				sb.append("-" + dateValues[1]);
				
				if(dateValues.length > 2)
					sb.append("-" + dateValues[2]);
				
			}
		}
		
		return sb.toString();
		/*Time time = new Time();
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
		*/
		
	}
}
