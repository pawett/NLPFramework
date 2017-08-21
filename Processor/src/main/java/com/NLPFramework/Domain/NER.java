package com.NLPFramework.Domain;

import java.io.Serializable;

public class NER extends Annotation implements Serializable 
{
	public String entityName;
	public EntityType type;
	
}
