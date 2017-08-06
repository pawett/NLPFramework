package com.NLPFramework.Domain;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Optional;



public class TokenizedFile extends LinkedList<TokenizedSentence> 
{
	
	private static final long serialVersionUID = 1L;
	private String docID;
	//private FilesType type;
	private Language language = Language.EN;
	private String name = null;
	private String originalText = null;
	public Hashtable<Class<? extends Annotation>,LinkedList<Annotation>> annotations = new Hashtable<>();
	
	public TokenizedFile(Language language, String name)
	{
		this.language = language;
		this.name = name;
	}
	
	public TokenizedFile(Language language, String name, String originalText)
	{
		this.language = language;
		this.name = name;
		this.originalText = originalText;
	}
	
	public void setDocId(String dID)
	{
		this.docID = dID;
	}
	
	public String getDocID()
	{
		return this.docID;
	}
	
	public String getOriginalText()
	{
		if(originalText == null)
			return this.toString();
		return originalText;
	}
	
	public void setOriginalText(String originalText)
	{
		this.originalText = originalText;
	}
	
	
	
	public void addAnnotation(Class<? extends Annotation> class1, Annotation annotation)
	{
		if(annotations.get(class1) == null)
			annotations.put(class1, new LinkedList<>());
	
		annotation.id = "" + (annotations.get(class1).size() + 1);
		annotations.get(class1).push(annotation);
	}
	
	public void addAnnotationWithId(Class<? extends Annotation> class1, Annotation annotation)
	{
		if(annotations.get(class1) == null)
			annotations.put(class1, new LinkedList<>());
	
		annotations.get(class1).push(annotation);
	}
	
	public <T extends Annotation> T getAnnotation(Class<? extends Annotation> class1, Annotation annotation)
	{
		Optional<Annotation> an =  annotations.get(class1).stream().filter(a -> a.equals(annotation)).findFirst();
		if(an.isPresent())
			return (T) an.get();
		
		return null;
	}
	
	
	
	/*public TokenFileFormatter getFormatter(IWordFormatter formatter)
	{
		return new TokenFileFormatter(this, formatter);
	}*/
	
	public String getName()
	{
		return name;
	}
			
	//TODO:Add correct implementation
	public Language getLanguage()
	{
		return language;
	}

	
	/*public void setIds()
	{
		int timexNumber = 0;
		int eventNumber = 0;
		for(TokenizedSentence sentence : this)
		{
			for(Word token : sentence)
			{
				if(token.element_type.startsWith("timex"))
				{
					token.id = "t"+timexNumber;
					timexNumber++;
				}else if(token.element_type.startsWith("event"))
				{
					token.id = "e" + eventNumber;
					eventNumber++;
				}
			}
		}
	}*/
	
	
	public boolean add(TokenizedSentence sentence)
	{
		if(!this.isEmpty())
		{
			this.getLast().next = sentence;
			sentence.prev = this.getLast();
		}
		this.addLast(sentence);
		return true;
	}
		

	/*public String toString(IWordFormatter formatter)
	{
		StringBuilder sb = new StringBuilder();
		int numSentence = 0;
		for(TokenizedSentence sentence : this)
		{
	
			sb.append(getFormatter(formatter).sentenceToString(sentence, numSentence));
			
			numSentence++;
		}		
		return sb.toString();
	}*/
	
	public ArrayList<Word> FlattenFile()
	{
		ArrayList<Word> flattenedDocument = new ArrayList<>();
		this.forEach((sentence) -> {
			sentence.forEach((token) -> {
				flattenedDocument.add(token);
			});
		});
		
		return flattenedDocument;
	}

	public Word getWordById(String id)
	{
		Word w = null;
		for(TokenizedSentence sentence : this)
		{
			w = sentence.getWordById(id);
			if(w != null)
				return w;
		}
		return null;
	}

}
