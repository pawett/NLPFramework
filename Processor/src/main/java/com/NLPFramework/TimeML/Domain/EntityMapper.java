package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.Domain.Annotation;

public class EntityMapper<T extends Annotation> implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int firstWordPosition;
	public int endWordPosition;
	public T element;
}
