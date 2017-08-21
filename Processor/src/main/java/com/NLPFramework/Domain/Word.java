package com.NLPFramework.Domain;

import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.IntSupplier;


import edu.stanford.nlp.semgraph.ISemanticGraphEdgeEql;

public class Word implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Word prev = null;
	public Word next = null;
	public Word govWord = null;
	public String id = "-";
	public String file = "-";
	public String sentence = "-";
	public int sentenceNumber = -1;
	public String phra_id = "-";
	public int sentencePosition = -1;
	public String syntbio = "-";
	public String synt = "-";
	public String roleconf = "-";
	public ArrayList<SemanticRole> semanticRoles = new ArrayList<>();
	public String semanticRoleIOB = "-";
	public String semanticRole = "-";
	public Word depverb = null;
	public String tense = "-";
	public String polarity = "-";
	public String mainp_position = "-";
	public String preposition = "-";
	public String time_IOB2 = "-";
	public String event_IOB2 = "-";
	public String mainphraseIOB = "iobph";
	
	public String syntacticTree = "-";
	public String extra2 = "-";
	public String extra1Rec = syntacticTree;
	public String extra2Rec = extra2;
	public String extra3 = "-";
	public String extra4 = "-";
	public String extra3Rec = extra3;
	public String extra4Rec = extra4;
	public String extra5 = "-";
	public String extra6 = "-";
	public String element_type_class = "-";
	public String rolesconf = "rc";
	public String element_type = "element";
	public String norm_type2 = "-";
	public String norm_type2_value = "-";
	
	public String word = "word";
	public String wordRec = word;
	public String pos = "pos";
	public String posRec = pos;
	public String lemma = "lemma";
	public String lemmaRec = lemma;
	public String wn = "wn";
	public String ner = "-";
	public boolean isVerb = false;
	
	public SemanticRole getSemanticRole()
	{
		SemanticRole returnSR = null;
		if(semanticRole.equals("TMP"))
		{
			returnSR = new SemanticRole();
			returnSR.argument = PropBankArgument.TMP;
			returnSR.IOB = semanticRoleIOB;
			return returnSR;
			
		}
	
		for(SemanticRole sr : semanticRoles)
		{
			if(sr.argument != null)
			{
				returnSR = sr;
			}
		}
		return returnSR;
	}
	
	public String toString()
	{
		return word;
	}
	
	public void setRoleFromText(String text)
	{
		SemanticRole role = new SemanticRole();
		if(text.equals("O"))
		{
			if(semanticRole.equals("-"))
			{
				semanticRole = "-";
				semanticRoleIOB ="O";
				role.argument = null;
				role.IOB = null;
			}
		}else
		{
			String[] values = text.split("-");
			semanticRole = values[values.length-1];
			semanticRoleIOB = values[0];
			role.argument = PropBankArgument.valueOf(values[values.length-1]);
			role.IOB = values[0];	
		}
		this.semanticRoles.add(role);
		
	}
	
	public Word()
	{
	
	}
	
	public boolean isBeginElement() {
		// TODO Auto-generated method stub
		return  ((time_IOB2.matches("B-.*")) 
				 || ((element_type.matches("event") && event_IOB2.matches("B-.*"))));
	}
	
}
