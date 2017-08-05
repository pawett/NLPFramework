package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;


public class TimeLink extends Link implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String origin;
	public MakeInstance eventInstance;
	public MakeInstance relatedToEventInstance;
	public Timex3 relatedToTime;
	public TimeLinkRelationType type = TimeLinkRelationType.VAGUE;
	
	public TimeLink()
	{
		
	}
	
	public TimeLink(String origin, MakeInstance eventInstance, MakeInstance relatedToEventInstance, Timex3 relatedToTime, TimeLinkRelationType type)
	{
		this.origin = origin;
		this.eventInstance = eventInstance;
		this.relatedToEventInstance = relatedToEventInstance;
		this.relatedToTime = relatedToTime;
		this.type = type;
	}
}
