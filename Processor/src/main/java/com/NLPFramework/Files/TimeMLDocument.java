package com.NLPFramework.Files;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.Signal;
import com.NLPFramework.TimeML.Domain.TimeFunctionInDocument;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.TimeType;
import com.NLPFramework.TimeML.Domain.Timex3;

import edu.stanford.nlp.time.Timex;

public class TimeMLDocument implements Serializable{
	public int Id;
	public List<TimeLink> links = new ArrayList<TimeLink>();
	public List<TimeLink> eDctLinks = new ArrayList<TimeLink>();
	public List<TimeLink> eTimexLinks = new ArrayList<TimeLink>();
	public List<TimeLink> eEventLinks = new ArrayList<TimeLink>();
	public List<TimeLink> subEventEventLinks = new ArrayList<TimeLink>();
	public LinkedList<Event> events = new LinkedList<>();
	public LinkedList<Timex3> timex3s = new LinkedList<>();
	public LinkedList<Signal> signals = new LinkedList<>();
	public LinkedList<MakeInstance> makeInstances = new LinkedList<>();
	
	
	public Timex3 DCT;

	public TimeMLDocument(String dct)
	{
		Timex3 creationTimex = new Timex3();
		creationTimex.id = "0";
		creationTimex.functionInDocument = TimeFunctionInDocument.CREATION_TIME;
		creationTimex.temporalFunction 	= false;
		creationTimex.type = TimeType.DATE;
		creationTimex.value = dct;
		DCT = creationTimex;

	}
	
	public TimeMLDocument(TimeMLFile tFile)
	{	
		DCT = tFile.getDCT();
		if(DCT == null)
		{
			Timex3 creationTimex = new Timex3();
			creationTimex.id = "0";
			creationTimex.functionInDocument = TimeFunctionInDocument.CREATION_TIME;
			creationTimex.temporalFunction 	= false;
			creationTimex.type = TimeType.DATE;
			creationTimex.value = new SimpleDateFormat("yyyy-MM-dd").format(DateTime.now());
			DCT = creationTimex;
		}
		
		tFile.stream().forEach((sentence) -> {
			if(sentence.annotations.keySet().contains(Timex.class))
			{
				for(Enumeration<Word> e = sentence.annotations.get(Timex3.class).keys(); e.hasMoreElements();)
				{
					Word w = e.nextElement();
					Timex3 time = (Timex3)sentence.annotations.get(Timex3.class).get(w).element;
					timex3s.add(time);
				}
			}
			
			if(sentence.annotations.keySet().contains(Event.class))
			{
				for(Enumeration<Word> e =  sentence.annotations.get(Event.class).keys(); e.hasMoreElements();)
				{
					Word w = e.nextElement();
					Event event =  (Event)sentence.annotations.get(Event.class).get(w).element;
					events.add(event);
				}
			}
			
			if(sentence.annotations.keySet().contains(Signal.class))
			{
				for(Enumeration<Word> e = sentence.annotations.get(Signal.class).keys(); e.hasMoreElements();)
				{
					Word w = e.nextElement();
					Signal signal = (Signal)sentence.annotations.get(Signal.class).get(w).element;
					signals.add(signal);
				}
			}
			
			
		});
		
	}

	
}
