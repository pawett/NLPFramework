package com.NLPFramework.Domain;

import java.util.ArrayList;

public class Coreference extends Annotation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int offset = 0;
	public Word word = null;
	public ArrayList<Coreference> coreferences = new ArrayList<>();
	public Coreference(Word word, int offset)
	{
		this.word = word;
		this.offset = offset;
	}
	
	public void addCoref(Coreference coref)
	{
		if(coref != null && !this.word.equals(coref.word) && !coreferences.stream().anyMatch(c -> c.word.equals(coref.word)))
			coreferences.add(coref);
	}
	
	public String printCurrent()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		Word nextWord = word.next;
		for(int i = 1 ; i < offset; i++)
		{
			sb.append(" " + nextWord);
			nextWord = nextWord.next;
		}
		
		return sb.toString();
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(word);
		Word nextWord = word.next;
		for(int i = 1 ; i < offset; i++)
		{
			sb.append(" " + nextWord);
			nextWord = nextWord.next;
		}
		
		sb.append(" in sentence " + (word.sentenceNumber + 1));
		
		if(coreferences.size() > 0)
		{
			sb.append(" references to: ");
			int numCoref = 0;
			for(Coreference c : coreferences)
			{
				if(numCoref > 0)
					sb.append(", ");
				sb.append(c.toString());
				numCoref++;
			}
		}
		
		
		return sb.toString();
	}

}
