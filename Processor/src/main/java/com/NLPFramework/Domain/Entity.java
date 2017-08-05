package com.NLPFramework.Domain;

public class Entity extends Annotation
{
	
	public Word word;
	public int offset;
	public PropBankArgument role;
	
	public Entity()
	{}
	
	public Entity(Word w, int offset, PropBankArgument arg)
	{
		this.word = w;
		this.offset = offset;
		this.role = arg;
	}
	
	
}
