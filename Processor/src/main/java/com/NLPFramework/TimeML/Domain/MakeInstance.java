package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Word;


public class MakeInstance extends Annotation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Event event;
	public EventAspect aspect;
	public EventTense tense;
	public POS pos;
	public Polarity polarity;
	public String modality;
	public Signal signal;
	public String cardinality;
	
	public MakeInstance(Word w, Event e)
	{
		id= "ei" + e.id;
		event = e;
		aspect = e.aspect;
		tense = e.tense;
		setPOS(w);
		setPolarity(w);
		setModality(w);
		
	}
	
	public MakeInstance(Event e)
	{
		if(e == null)
			Logger.WriteDebug("Event does not exists");
		else
		{
		event = e;
		aspect = e.aspect;
		tense = e.tense;
		}
	}

	private void setModality(Word w) {
		
		if(w.tense.contains("conditional"))
		{
			if(w.govWord != null)
				modality = w.govWord.word.toLowerCase();
		}
		
	}

	private void setPolarity(Word w)
	{
		switch(w.polarity)
		{
		case "positive":
			polarity = Polarity.POS;
			break;
		case "negative":
			polarity = Polarity.NEG;
		default:
			polarity = Polarity.POS;
		}
		
	}

	private void setPOS(Word w) 
	{
		if(w.pos.startsWith("V"))
			pos = POS.VERB;
		
		if(w.pos.startsWith("N"))
			pos = POS.NOUN;
		
		if(w.pos.startsWith("J"))
			pos = POS.ADJECTIVE;
		
		if(w.pos.startsWith("?"))
			pos = POS.PREP;
		
	}
}
