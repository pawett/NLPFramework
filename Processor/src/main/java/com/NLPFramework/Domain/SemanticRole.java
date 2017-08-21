package com.NLPFramework.Domain;

import java.io.Serializable;

public class SemanticRole extends Annotation implements Serializable {
	//public Word verb = null;
	public PropBankArgument argument = null;
	public String IOB="O";
	public TokenizedSentence words = new TokenizedSentence();
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		//if(verb != null)
		//	sb.append(verb.lemma + "_");
		sb.append(argument + "_");
		sb.append(IOB);
		sb.append(" ");
		sb.append(words.toString());
		return sb.toString();
	}
}
