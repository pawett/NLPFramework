package com.NLPFramework.Domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;

import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.Signal;
import com.NLPFramework.TimeML.Domain.Timex3;
import com.NLPFramework.TimeML.Train.TimeMLTrainTypes;

public class TokenizedSentence  extends LinkedList<Word>{
	
	public TokenizedSentence prev;
	public TokenizedSentence next;
	public String originalText;
	public String synt;
	public LinkedList<TokenizedSentence> subSentences = new LinkedList<>();
	public ArrayList<Word> verbs = new ArrayList<>();
	/*public Hashtable<Word,ArrayList<Word>> events = new Hashtable<Word, ArrayList<Word>>();
	public Hashtable<Word,ArrayList<Word>> times = new  Hashtable<Word,ArrayList<Word>>();
	*/
	public Hashtable<Class<Annotation>,Hashtable<Word, EntityMapper<Annotation>>> annotations = new Hashtable<>();
	public boolean add(Word word)
	{
		
		if(!this.isEmpty())
		{
			Word previousLast = this.getLast();
			previousLast.next = word;
			word.prev = previousLast;
		}
		//word.tok_num = this.size() + "-s";
		this.addLast(word);
		return true;
	}
	
	public String getOriginalText()
	{
		return originalText;
	}
	
	public Word getSubsentenceVerb(TokenizedSentence sentence)
	{
		if(sentence == null)
			return null;
		if(sentence.verbs != null && sentence.verbs.size() > 0 && sentence.verbs.get(0).isVerb)
			return sentence.verbs.get(0);
		else if(sentence.prev != null)
			return getSubsentenceVerb(sentence.prev);
		
		return null;
				
	}
	
	public Word getWordDependantVerb(Word w)
	{
		TokenizedSentence sentence = getWordSubSentence(w);
		return getSubsentenceVerb(sentence);
	}
	
	public TokenizedSentence getWordSubSentence(Word w)
	{
		TokenizedSentence wordSubSentence = null;
		if(!this.synt.equalsIgnoreCase("ROOT") && this.contains(w))
			return this;
		for(TokenizedSentence s : this.subSentences)
		{
			TokenizedSentence subSentence = s.getWordSubSentence(w);
			if(subSentence != null)
				return subSentence;
				//wordSubSentence = subSentence;
			/*if(s.contains(w))
				wordSubSentence = s;
			else
			{
				TokenizedSentence subSentence = s.getWordSubSentence(w);
				if(subSentence != null)
					wordSubSentence = s.getWordSubSentence(w);
			}*/
		}
		return wordSubSentence;
	}

	public boolean isWordInSubsentence(Word w)
	{
		boolean isInSubsentence = false;

		for(TokenizedSentence previous = this.prev; previous != null; previous = previous.prev)
		{
			if(previous != null && previous.synt != null && previous.synt.equalsIgnoreCase("S"))
				isInSubsentence = true;
		}
		
		return isInSubsentence;
	}
	
	public void addAnnotation(Class<? extends Annotation> class1, Word w, EntityMapper<? extends Annotation> eMap)
	{
		if(annotations.get(class1) == null)
			annotations.put((Class<Annotation>) class1, new Hashtable<>());
		
		eMap.element.id = String.valueOf(annotations.get(class1).size());
	
		annotations.get(class1).put(w, (EntityMapper<Annotation>) eMap);
	}
	
	public void addAnnotationWithId(Class<? extends Annotation> class1, Word w, EntityMapper<? extends Annotation> eMap)
	{
		if(annotations.get(class1) == null)
			annotations.put((Class<Annotation>) class1, new Hashtable<>());
		
		
		annotations.get(class1).put(w, (EntityMapper<Annotation>) eMap);
	}
	
	public Event getEventByWord(Word w)
	{
		return (Event)annotations.get(Event.class).get(w).element;
	}
	
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Word w : this)
		{
			sb.append(w.word + " ");
		}
		return FileHelper.formatText(sb.toString());
	}
	
	public String toStringSynt()
	{
		Hashtable<String,String> values = new Hashtable<>();
		for(TokenizedSentence s : this.subSentences)
			s.toStringSynt(0, values);
		
		StringBuilder sb = new StringBuilder();
		int i = 0;
		while(values.containsKey(String.valueOf(i)))
		{
			sb.append(values.get(String.valueOf(i)));
			sb.append(System.lineSeparator());
			i++;
		}
		
		return sb.toString();
	}
	
		
	public String toStringSyntFlat()
	{
		StringBuilder sb = new StringBuilder();
		
		if(this.synt != null && this.synt != "ROOT")
		{
			sb.append("(" + this.synt + ":: ");
			for (Word w : this)
			{
				sb.append(w.word + " ");
			}
			/*sb.append(" -- verbs: ");
			for(Word v : this.verbs)
				sb.append(v.word + " ");*/
		}
		
		for(TokenizedSentence s : this.subSentences)
		{	
			sb.append(s.toStringSyntFlat() + " ");
		}
		
	
		if(this.synt != null && this.synt != "ROOT")
		sb.append(")");
		return sb.toString();
	}
	
	public void toStringSynt(int level, Hashtable<String,String> values )
	{
		StringBuilder sb = new StringBuilder();
		if(values.containsKey(String.valueOf(level)))
			sb.append(values.get(String.valueOf(level)));
		sb.append(level + " ( " + this.synt);
		String currentValue = "";
		for (Word w : this)
		{
			sb.append(w.word + " ");
		}
		sb.append(" -- verbs: ");
		for(Word v : this.verbs)
			sb.append(v.word + " ");

		values.put(String.valueOf(level), sb.toString());

		for(TokenizedSentence s : this.subSentences)
		{	
			s.toStringSynt(++level, values);
		}
		sb.append(")");

	}
	
	
	public Word getWordById(String id) {
		for(Word w : this)
		{
			if(w.id.equals(id))
				return w;
		}
		return null;
	}

	public void cleanAnnotations(TimeMLTrainTypes annotation) 
	{
		switch(annotation)
		{
		case recognition:
			//events = new Hashtable<Word, ArrayList<Word>>();
			if(annotations.get(Event.class) != null)
			annotations.get(Event.class).clear();
			//times = new Hashtable<>();
			if(annotations.get(Timex3.class) != null)
			annotations.get(Timex3.class).clear();
			break;
		case classification:
			if(annotations.get(Event.class) != null)
			{
				for(Word w : annotations.get(Event.class).keySet())
				{
					Event event = (Event) annotations.get(Event.class).get(w).element;
					event.eventClass = null;
				}
			}
			
			if(annotations.get(Timex3.class) != null)
			{
				for(Word w : annotations.get(Timex3.class).keySet())
				{
					Timex3 timex = (Timex3)annotations.get(Timex3.class).get(w).element;
					timex.type = null;
				}
			}
			
			break;
		default:
			//events = new Hashtable<Word, ArrayList<Word>>();
			//times = new  Hashtable<Word,ArrayList<Word>>();
			annotations.get(Event.class).clear();
			annotations.get(Timex3.class).clear();
			annotations.get(Signal.class).clear();
			
			break;
		}
		
	}

	public void assignVerb(Word token) 
	{
		if(this.synt.equals("S"))
		{
			this.verbs.add(token);
			return;
		}else if(this.prev != null)
		{
			this.prev.assignVerb(token);
		}
		/*if(this.verbs.size() == 0)
			this.verbs.add(token);
		for(Word w: this)
		{
			w.depverb = token;// TODO Auto-generated method stub
		}
		if(this.prev != null)
			this.prev.assignVerb(token);*/
		
	}
	

	public Word getDependantVerb()
	{
		if(this.synt.equals("S") || this.synt.equals("ROOT"))
			return this.verbs.size() > 0 ? this.verbs.get(0) : null;
		if(this.synt.contains("V") && this.size() > 0 && this.get(0).isVerb)
			return this.get(0);
		else if(this.prev != null)
			return this.prev.getDependantVerb();
		return null;
	}
	
	public boolean isSentenceStart()
	{
		return this.synt.equals("S");
	}
	
	public int getNumSentencesAbove()
	{
		int numSent = 0;
		TokenizedSentence sentence = this;
		while(sentence != null)
		{
			if(sentence.isSentenceStart())
				numSent++;
			sentence = sentence.prev;
		}
		return numSent;
	}
	
	public int getNumSentencesAbove(Word w)
	{
		TokenizedSentence sentence = getWordSubSentence(w);
		return sentence.getNumSentencesAbove();
	}
	
	public int getNumPhrasesAbove(Word w)
	{
		TokenizedSentence sentence = getWordSubSentence(w);
		int numPhrases = 0;
		while(sentence != null)
		{
			sentence = sentence.prev;
			numPhrases++;
		}
		return numPhrases;
	}
	
	public String getWordPP(Word w)
	{
		/*TokenizedSentence sentence = getWordSubSentence(w);
		while(sentence != null)
		{
			if((sentence.synt.startsWith("PP") || sentence.synt.startsWith("SBAR") || sentence.synt.startsWith("WH")) && sentence.size() > 0)
				return sentence.get(0).lemma;
				
			if(sentence.synt.startsWith("SBAR"))
				return "-";
			sentence = sentence.prev;
		}*/
		Word iterator = new Word();
		iterator = w;
		while(iterator.prev != null)
		{
			iterator = iterator.prev;
			if(iterator.pos.equalsIgnoreCase("IN"))
				return iterator.lemma;
			
		}
		return "-";
	}
	
	public boolean isMainSentence(Word w)
	{
		int numSent = getNumSentencesAbove(w);
		
		if(numSent > 1)
			return false;
		return true;
	}
	
	public void fillVerbs(Word verb)
	{
		if(this.synt == "S")
			verb = null;
		for(Word w : this)
		{
			if(w.isVerb)
				verb = w;
			
		}
				
		
		for(TokenizedSentence s : this.subSentences)
		{
			if(s.verbs.size() == 0 && verb != null)
				s.verbs.add(verb);
			else if(s.verbs.size() > 0)
				verb = s.verbs.get(0);
			s.fillVerbs(verb);
		}
		
		// TODO Auto-generated method stub
		
	}

	
	
	
}
