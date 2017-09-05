package com.NLPFramework.Processor;

import java.io.IOException;
import java.util.LinkedList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.WNInterface;
import com.NLPFramework.Helpers.Constants;

public class FeatureExtractorEnglish implements IFeatureExtractorStrategy {

	@Override
	public TokenizedFile setFeatures(TokenizedFile file) 
	{  	
    	try 
    	{  		
    		//get sentence verbs
    		TokenizedSentence currentSubsentence = null;
    		TokenizedSentence newSubsentence = null;
    		processVerbs(file);
    		syntacticParser(file);
    		   		
    		
    		
    		//  while ((line = pipesreader.readLine()) != null) 
    		for(TokenizedSentence sentenceInFile : file)
    		{
    			//Sentence stndSentence = new Sentence (sentenceInFile.toOriginalText());
    			int phraId = 0;
    			int sentenceId = 0;
    			//Tree t = stndSentence.parse();
    			//setSyntHierarchy(t, sentenceInFile);
    			LinkedList<Word> verbsInOrder = new LinkedList<>();
    			String currentSentence = "";
    			for(Word token : sentenceInFile)             
    			{	
    				try 
    				{
						processSemanticRoles(token, verbsInOrder, sentenceInFile);
						if(!token.sentence.equals(currentSentence))
							verbsInOrder = new LinkedList<>();
						currentSentence = token.sentence;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}   		
    		}

    	} catch (Exception e) {
    		Logger.WriteError("Errors found (TIMEE):\n\t", e);
    		System.exit(1);
    		return null;
    	}
    	return file;
	}


	private void processVerbs(TokenizedFile file) 
	{
		for(TokenizedSentence sentenceInFile : file) 
		{
			LinkedList<Word> verbPhrase = new LinkedList<>();
			String currentSentence = "";
			String polarity = "positive";
			for(Word token : sentenceInFile)             
			{	
				
				if(token.lemma.equalsIgnoreCase("have") && token.next != null && token.next.lemma.equalsIgnoreCase("to"))
				 {
					token.isVerb = false;
					continue;
				 }
				
				if (token.pos.equalsIgnoreCase("rb") && token.lemma.equalsIgnoreCase("not"))
				{
					polarity = "negative";
					for(Word verb : verbPhrase)
					{
						verb.polarity = polarity;
					}
				}
				
				if (token.pos.matches("(V|N|J).*")) 
				{	
					
					WNInterface wn = new WNInterface(Language.EN);
					String hypersString = wn.getHypers(token.word, token.pos);
					if(hypersString == null || hypersString.isEmpty())
						token.wn = "-";
					else
					{
						String[] hypers =  hypersString.split(">");
						token.wn = hypers[hypers.length-1];
					}

				} else {
					token.wn = "-";
				}
				if(token.syntbio.equals("B") && token.synt.equals("VP"))
				{
					polarity = "positive";
					 Word firstVerb = verbPhrase != null && verbPhrase.size() > 0 ? verbPhrase.getFirst() : null;
					 
					 if(firstVerb != null && firstVerb.pos.matches("MD") && firstVerb.lemma.matches("(?:can|could|would|may|might|should|must)"))
					 {
						 token.tense = "modal-" + token.tense;
					 }
					verbPhrase = new LinkedList<>();
				
				}
				Word lastVerb = new Word();
				if(!verbPhrase.isEmpty())
					lastVerb = verbPhrase.getLast();

				if (token.pos.startsWith("V") || token.pos.matches("MD")) 
				{			
					processVerb(token, verbPhrase);
					token.polarity = polarity;
					if(token.tense.contains("infinitive"))
					{
						if(lastVerb != null && !lastVerb.tense.contains("infinitive"))
							token.tense = lastVerb.tense + "-infinitive";
						token.polarity = lastVerb.polarity;
						verbPhrase = new LinkedList<>();//avoid linked infinitives starting to think to work here
					}
					if(token.tense.isEmpty())
						token.tense = "-";
					
					verbPhrase.add(token);					

					lastVerb = token;		


				}
				if(token.next == null)//last word of sentence
				{
					 Word firstVerb = verbPhrase != null && verbPhrase.size() > 0 ? verbPhrase.getFirst() : null;
					 
					 if(firstVerb != null && firstVerb.pos.matches("MD") && firstVerb.lemma.matches("(?:can|could|would|may|might|should|must)"))
					 {
						 if(firstVerb != lastVerb)
							 lastVerb.tense = "modal-" + lastVerb.tense;
					 }
				}
			} 
			
		}
	}


	private void syntacticParser(TokenizedFile file) 
	{
		TokenizedSentence currentSubsentence = null;
		
		for(TokenizedSentence sentenceInFile : file)
		{
			//int phraId = 0;
			int sentenceId = 0;
			String IOBPhra="B";
			String IOBSentence="B";
			currentSubsentence = sentenceInFile;// new TokenizedSentence();
			sentenceInFile.synt = "ROOT";

			Word currentVerb = null;
			for(Word token : sentenceInFile)             
			{
				sentenceInFile.toStringSynt();

				String[] values = token.syntacticTree.split("\\(");
				int openBrackets = token.syntacticTree.length() - token.syntacticTree.replaceAll("\\(", "").length();
				if(openBrackets > 0)
				{
					//Logger.Write("Setting subsentences");
					for(int i = 1; i < values.length ; i++)
					{

						TokenizedSentence newSentence = new TokenizedSentence();
						newSentence.synt = values[i].replaceAll("\\)", "");
						currentSubsentence.subSentences.addLast(newSentence);
						currentSubsentence.next = newSentence;
						newSentence.prev = currentSubsentence;
						currentSubsentence = newSentence;

					}
					//Logger.Write("after Setting subsentences");
				}
				
				TokenizedSentence newSentence = new TokenizedSentence();
				newSentence.prev = currentSubsentence;
				newSentence.add(token);
				newSentence.synt = token.pos;
				currentSubsentence.subSentences.add(newSentence);
				//currentSubsentence.add(token);
				if(token.isVerb)
				{
					currentVerb = token;
					sentenceInFile.verbs.add(token);
					//currentSubsentence.verbs.add(token);
					currentSubsentence.assignVerb(token);
					
				}
				
				values = token.syntacticTree.split("\\)");
				int closeBrackets = token.syntacticTree.length() - token.syntacticTree.replaceAll("\\)", "").length();
				for(int i = 0; i < closeBrackets ; i++)
				{
					// Logger.WriteDebug("Closing brackets");
					if(currentSubsentence.equals(sentenceInFile))
					{
						currentSubsentence = sentenceInFile.subSentences.getFirst();
						break;
					}
					currentSubsentence = currentSubsentence.prev;
					if(currentSubsentence == null || currentSubsentence.equals(sentenceInFile))
					{
						currentSubsentence = sentenceInFile.subSentences.getFirst();
						break;
					}
				}				
			}
		}
	}
	
	
	private void processSemanticRoles(Word token, LinkedList<Word> verbsInOrder, TokenizedSentence currentSentence) throws IOException
	{
		
		Word depVerb = currentSentence.verbs != null && currentSentence.verbs.size() > 0 ? currentSentence.verbs.get(0) : null;
		
		TokenizedSentence contextSentence = currentSentence.getWordSubSentence(token);
		if(contextSentence != null && contextSentence.getWordDependantVerb(token) != null)// && contextSentence.verbs.size() > 0)
		 depVerb = contextSentence.getWordDependantVerb(token);//.verbs.get(0);
		token.depverb = depVerb;
		
		int pos = currentSentence.verbs.indexOf(depVerb);
		if(pos < 0)
			pos = 0;
		if(token.semanticRoles != null && token.semanticRoles.size() > pos &&  token.semanticRoles.get(pos) != null && token.semanticRoles.get(pos).argument != null)
		{
			token.semanticRole = token.semanticRoles.get(pos).argument.toString();
			token.semanticRoleIOB = token.semanticRoles.get(pos).IOB;
		}
	
		
		token.mainphraseIOB = currentSentence.isMainSentence(token) ? "I" : "O";
		token.sentence = String.valueOf(currentSentence.getNumSentencesAbove(token));
		
		token.preposition = currentSentence.getWordPP(token);
		token.phra_id = String.valueOf(currentSentence.getNumPhrasesAbove(token));
		
		/*if(token.prev != null)
			pp = token.prev.preposition;
		if (token.pos.matches("(IN|TO)")) 
		{
			pp = token.lemma;		  
		}
		*/

		//token.preposition = pp;


		return;
	}
	
	
	private static void processVerb(Word token, LinkedList<Word> verbsInOrder)
	{			
		Word previousVerb = null;
		if(!verbsInOrder.isEmpty())
			previousVerb = verbsInOrder.getLast();
		Word secondPreviousVerb = verbsInOrder.size() >= 2 ? verbsInOrder.get(verbsInOrder.size()-2) : null;
		Word thirdPreviousVerb = verbsInOrder.size() >= 3 ? verbsInOrder.get(verbsInOrder.size()-3) : null;;
		
		token.tense = "-";	
		
		if(previousVerb != null && previousVerb.lemma.matches("do")) //in the case of the did modifier, all the sentence is in past
		{
			previousVerb.isVerb = false;
			if(previousVerb.pos.matches("VBD"))//did
			{
				token.tense = previousVerb.tense;
				return;
			}	
			//token.relEvents.add(lastVerb);
		}
		
		 if(token.pos.matches("MD"))
		 {		 
			token.tense = "AUX";
		 	//return;
		 }
		 
		
		 if(token.pos.matches("VB|VB(?:P|Z)"))//VB base form
		 {
			 //Case Base: I work everyday
			 token.tense = "present";
			 if(token.prev != null && token.prev.lemma.equalsIgnoreCase("to"))
			 {
				 if(previousVerb != null)
				 {
					 if(!previousVerb.lemma.matches("have"))
						 token.tense = "simple-infinitive"; // I wanted to work
				 }
				 else
				 {
					 //expresses purpose
					 //He worked very hard to earn more money
				 }
			 }
			 
			 if(previousVerb != null)
			 {
				 
				 if(previousVerb.pos.matches("MD") && previousVerb.word.equalsIgnoreCase("will"))
				 {
					 //I will work tomorrow
					 token.tense = "future";
					 previousVerb.isVerb = false;
					// previousVerb.tense = token.tense;
				 }else if(previousVerb.prev != null)
				 {
					 if(previousVerb.tense.equalsIgnoreCase("present-continuous") && previousVerb.lemma.equalsIgnoreCase("go"))
					 {
						 //I am going to work
						 token.tense = "future";
						 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
					 }	 
				 }
			 }
			 //return;
		 }
		 
		 
		 if(token.pos.matches("VBG"))
		 {
			 
			 if(token.prev != null && token.prev.pos.matches("IN"))
			 {
				 //While working
				 token.tense = "present-continuous";
				// return;
			 }else  if(previousVerb != null)
			 {
				 if(previousVerb.lemma.equalsIgnoreCase("be"))
				 {
					 if (previousVerb.tense == "present")
					 {
						 if(token.prev != null && token.prev.prev != null & token.prev.prev.lemma.equalsIgnoreCase("to"))
							 token.tense = "continuous-infinitive"; //to be working
						 else
						 {
							 //I am working
							 token.tense = "present-continuous";
							 previousVerb.isVerb = false;
						 }
						// return;
					 }
					 
					 if(previousVerb.tense.equalsIgnoreCase("present-perfect"))
					 {
						 //I have been working
						 token.tense = "present-perfect-continuous";
						 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						 //return;
					 }
	
					 if(previousVerb.tense.equalsIgnoreCase("past-perfect"))
					 {
						 //I had been working
						 token.tense = "past-perfect-continuous";
						 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						// return;
					 }
					 if(previousVerb.tense == "past")
					 {
						 //I was working
						 token.tense = "past-continuous";
						 previousVerb.isVerb = false;
						 //return;
					 }
					 
					 if(previousVerb.tense == "future-perfect")
					 {
						 //I will have been working
						 token.tense = "future-perfect-continuous";
						 thirdPreviousVerb.isVerb = secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						// return;
					 }

					 if(previousVerb.tense.equalsIgnoreCase("conditional-present"))
					 {
						 token.tense = "conditional-present-continuous";
						 previousVerb.isVerb = false;
					 }
				 }
			 }
			 
			 if(token.tense.equalsIgnoreCase("-"))
			 {
				 if(previousVerb != null)
				 {
					 token.tense = previousVerb.tense + "-" + "gerund";
					 //if(!previousVerb.lemma.matches("stop|begin|start|finish"))
						 previousVerb.isVerb = false;
				 }
				 else
					 token.tense = "gerund";
			 //Case Base: Is a noun. Working late is the worst.
			 //token.pos= "NN";
			 //token.isVerb = false;
			 
			// SetVerbAssertType(token);
			// return;
			 }
		 }
		 
		 if(token.pos.matches("VB(?:D|N)"))
		 { 
			 //Case Base: I worked
			 token.tense="past";

			 if(previousVerb != null)
			 {
				 if(previousVerb.pos.matches("VB|VB(?:P|Z)"))
				 {
					 if(previousVerb.word.matches("ha(?:s|ve)"))
					 { 
						 if(secondPreviousVerb != null && secondPreviousVerb.word.equals("will"))
						 { //I will have worked
							 token.tense = "future-perfect";
							 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						 }else
						 {
							 if(token.prev != null && token.prev.prev != null && token.prev.prev.lemma.equalsIgnoreCase("to"))
								 token.tense = "perfect-infinitive"; //to have worked
							 else
							 {
								 //I/He have/has worked till 6 o'clock
								 token.tense = "present-perfect";
								 previousVerb.isVerb = false;
							 }
						 }
					 }
					 if(previousVerb.word.matches("is|are"))
					 {
						 //is worked
						 token.tense = "passive-present"; 
						 previousVerb.isVerb = false;
					 }
					 
				 }else if(previousVerb.pos.matches("VBD"))
				 {
					 if(previousVerb.word.matches("was|were")) //was worked
						 token.tense = "passive-past";
					 else //I had worked
					 token.tense = "past-perfect";
					
					 previousVerb.isVerb = false;

				 }else if(previousVerb.pos.matches("VBG"))
				 {
					 if(previousVerb.word.matches("being"))
					 {
						 if(secondPreviousVerb != null && secondPreviousVerb.word.matches("is|are"))
						 { 
							//is being worked
							 token.tense = "passive-present-continuous"; 
							 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						 }
					 }
				 }else if(previousVerb.pos.matches("VBN"))
				 {
					 if(secondPreviousVerb != null)
					 {
						 if(secondPreviousVerb.word.matches("have|has"))
						 { 
							 if(secondPreviousVerb.prev != null && secondPreviousVerb.prev.word.equalsIgnoreCase("would"))
								 token.tense = "passive-past-conditional";//would have been worked
							 else
								 token.tense = "passive-present-perfect"; //have been worked
							 
							 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
							 
						 }else if(secondPreviousVerb.word.matches("had"))
						 {
							 token.tense = "passive-past-perfect"; //had been worked
							 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						 }
					 }
				 }else if(previousVerb.lemma.matches("be"))
				 {
					 if(secondPreviousVerb != null)
					 {
						 if (secondPreviousVerb.pos.matches("MD"))
						 {

							 if(secondPreviousVerb.word.equalsIgnoreCase("will"))
							 {
								 token.tense = "passive-future"; //will be worked
							 }else if(secondPreviousVerb.word.equalsIgnoreCase("would"))
								 token.tense = "pasive-past-conditional"; //would be worked
							 else
								 token.tense = "passive-modal";//must/can be worked
							 
							 secondPreviousVerb.isVerb = previousVerb.isVerb = false;
						 }
					 }else if(previousVerb.prev != null && previousVerb.lemma.equalsIgnoreCase("to"))
					 { 
						 token.tense = "passive-infinitive"; //to be worked
						 previousVerb.isVerb = false;
					 }
				 }
				 

				// return;
			 }
		 }
		 
		
		 if(previousVerb != null && 
				 previousVerb.pos.matches("MD") &&
				 previousVerb.lemma.toLowerCase().matches(Constants.matchModal))
		 {
			 token.tense = "modal-" + token.tense;
			 previousVerb.isVerb = false;
		 }
		 if(  secondPreviousVerb != null && 
				 secondPreviousVerb.pos.matches("MD") &&
				 secondPreviousVerb.lemma.toLowerCase().matches(Constants.matchModal))
		 {
			 token.tense = "modal-" + token.tense;
			 secondPreviousVerb.isVerb = false;
		 }
		 if(thirdPreviousVerb != null && 
				 thirdPreviousVerb.pos.matches("MD") &&
				 thirdPreviousVerb.lemma.toLowerCase().matches(Constants.matchModal))
		 {
			 token.tense = "modal-" + token.tense;
			 thirdPreviousVerb.isVerb = false;
		 }
		 
		 Word firstVerb = verbsInOrder != null && verbsInOrder.size() > 0 ? verbsInOrder.getFirst() : null;
		 if(firstVerb  != null && firstVerb.lemma.equals("have") && firstVerb.next != null && firstVerb.next.lemma.equalsIgnoreCase("to"))
		 {
			 firstVerb.isVerb = false;
			 token.tense = "modal-have-" + token.tense;
		 }
	}

}
