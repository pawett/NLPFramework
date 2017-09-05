package com.NLPFramework.Domain;

import java.io.Serializable;

public class NER extends Annotation implements Serializable 
{
	public String entityName;
	public EntityType type = EntityType.NONE;
	public int offset = 0;
	public Word word = null;
	
	public String toString()
	{
		return entityName;
	}
}
