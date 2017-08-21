package com.NLPFramework.Domain;

import java.util.ArrayList;

public class Entity extends Annotation
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5710724068914269622L;
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
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		Word nextWord = word.next;
		for(int i = 1 ; i < offset && nextWord != null; i++)
		{
			sb.append(" " + nextWord);
			nextWord = nextWord.next;
		}
		return sb.toString();
	}
	
	public ArrayList<Word> getWords()
	{
		ArrayList<Word> words = new ArrayList<>();
		words.add(word);
		Word nextWord = word.next;
		for(int i = 1 ; i < offset && nextWord != null; i++)
		{
			words.add(nextWord);
			nextWord = nextWord.next;
		}
		
		return words;
	}
	
	
}
