package com.NLPFramework.NewsReader.Domain;

import com.NLPFramework.Domain.Word;

public class EventMention extends com.NLPFramework.TimeML.Domain.Event {

	public EventMention(Word w) {
		super(w);
		// TODO Auto-generated constructor stub
	}
	
	public EventMention()
	{
		super();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Certainty certainty = null;
	public Factuality factuality = null;
	public String modality = null;
}
