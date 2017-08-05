package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Word;


public class Event extends Annotation implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public EventClass eventClass = EventClass.OCCURRENCE;
	public String stem;
	public EventTense tense;
	public EventAspect aspect;
	public Word word;
	
	public Event(Word w)
	{
		//id = w.id;
		stem = w.word;
	
		if(!w.element_type_class.equals("-"))
			eventClass = EventClass.valueOf(w.element_type_class);
		setEventTense(w.tense);
		setEventAspect(w);
		word = w;
	}
	
	public Event() {
		// TODO Auto-generated constructor stub
	}

	private void setEventAspect(Word w)
	{
		aspect = EventAspect.NONE;
		if(w.tense.contains("perfect-continuous"))
			aspect = EventAspect.PERFECTIVE_PROGRESSIVE;
		else if(w.tense.contains("perfect"))
			aspect = EventAspect.PERFECTIVE;
		else if(w.tense.contains("continuous"))
			aspect = EventAspect.PROGRESSIVE;
	}
	
	private void setEventTense(String tense)
	{
		this.tense = EventTense.NONE;
				
		if(tense.contains("future"))
			this.tense = EventTense.FUTURE;
		
		if(tense.contains("present"))
		{
			if(tense.endsWith("perfect"))
				this.tense = EventTense.PRESPART;
			else
				this.tense = EventTense.PRESENT;
		}
		
		if(tense.contains("past"))
		{
			if(tense.endsWith("perfect"))
				this.tense = EventTense.PASTPART;
			else
				this.tense = EventTense.PAST; 
		}
		
		if(tense.contains("inifinitive"))
			this.tense = EventTense.INFINITIVE;
		
		if(tense.contains("conditional"))
			this.tense = EventTense.NONE;
	}
}
