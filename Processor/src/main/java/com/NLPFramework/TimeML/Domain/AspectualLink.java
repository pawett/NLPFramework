package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.TimeML.Domain.Link;

public class AspectualLink extends Link implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Event eventInstance;
	public Event relatedToEventInstance;
	public AspectualLinkType relType;
	
	public AspectualLink(String id, Event eventInstance, Event relatedToEventInstance, AspectualLinkType type, Signal signal)
	{
		this.id = id;
		this.eventInstance = eventInstance;
		this.relatedToEventInstance = relatedToEventInstance;
		this.relType = type;
		this.signal = signal;
	}
}
