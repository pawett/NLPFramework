package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

public class SubordinationLink extends Link implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Event eventInstance;
	public Event subordinateEventInstance;
	public SubordinationLinkType type;

	public SubordinationLink(String id, Event eventInstance, Event subordinateEventInstance, SubordinationLinkType type, Signal signal)
	{
		this.id = id;
		this.eventInstance = eventInstance;
		this.subordinateEventInstance = subordinateEventInstance;
		this.type = type;
		this.signal = signal;
	}
}
