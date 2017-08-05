package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.Domain.Annotation;

public abstract class Link extends Annotation implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String syntax;
	public Signal signal;
}
