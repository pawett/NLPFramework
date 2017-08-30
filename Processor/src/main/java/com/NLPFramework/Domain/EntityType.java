package com.NLPFramework.Domain;

public enum EntityType 
{
	PERSON,ORGANIZATION,PRODUCT,FINANCIAL,NONE;

	public static EntityType getEntityTypeFromText(String text)
	{
		if(text.startsWith("PER"))
			return PERSON;
		if(text.startsWith("ORG"))
			return EntityType.ORGANIZATION;
		
		return NONE;
	}
}
