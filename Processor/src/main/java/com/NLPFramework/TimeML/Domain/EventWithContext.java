package com.NLPFramework.TimeML.Domain;

public class EventWithContext extends Event 
{
	private static final long serialVersionUID = 1L;
	public String documentId = "";
	public int sentenceNumber = -1;
	
	public String toString()
	{
		return documentId + "-" + (sentenceNumber - 1) + "-" + stem;
	}
}
