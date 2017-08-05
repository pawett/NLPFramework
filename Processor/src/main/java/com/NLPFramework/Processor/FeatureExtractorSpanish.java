package com.NLPFramework.Processor;

import java.io.IOException;
import java.util.LinkedList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.externalTools.WNInterface;


public class FeatureExtractorSpanish implements IFeatureExtractorStrategy {

	@Override
	public TokenizedFile setFeatures(TokenizedFile file) 
	{
    	
    	try 
    	{  		
    		//get sentence verbs
    		file.parallelStream().forEach((sentenceInFile) -> 
    		{
    			for(Word token : sentenceInFile)             
    			{
    				if(token.isVerb)
    					sentenceInFile.verbs.add(token);
    			}
    		});
    		//  while ((line = pipesreader.readLine()) != null) 
    		file.parallelStream().forEach((sentenceInFile) -> 
    		{
    			//Sentence stndSentence = new Sentence (sentenceInFile.toOriginalText());
    			int phraId = 0;
    			int sentenceId = 0;
    			//Tree t = stndSentence.parse();
    			//setSyntHierarchy(t, sentenceInFile);
    			LinkedList<Word> verbsInOrder = new LinkedList<>();
    			String pp ="-";
    			for(Word token : sentenceInFile)             
    			{	
    				if(token.syntbio.startsWith("B"))
    					phraId++;
    				if(token.syntbio.startsWith("S"))
    				{
    					sentenceId++;
    				}
    				token.phra_id = String.valueOf(phraId);
    				token.sentence = String.valueOf(sentenceId);
    				try {
						ProcessWord(token, verbsInOrder, pp, sentenceInFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
    			}   		
    		});

    	} catch (Exception e) {
    		Logger.WriteError("Errors found (TIMEE):\n\t", e);
    		System.exit(1);
    		return null;
    	}
    	return file;
	}
	
	
	private void ProcessWord(Word token, LinkedList<Word> verbsInOrder, String pp, TokenizedSentence currentSentence) throws IOException
	{
		int verbPos = 0;
		for(Word verb : currentSentence.verbs)
		{
			//if(!token.semanticRoles.isEmpty() && token.semanticRoles.size() > verbPos)
			//	token.semanticRoles.get(verbPos).verb = verb;
			verbPos++;
		}


		if(token.prev != null)
			pp = token.prev.preposition;
		if (token.pos.matches("(IN|TO)")) 
		{
			pp = token.lemma;		  
		}

		if (token.pos.matches("(V|N).*")) 
		{	
			WNInterface wn = new WNInterface(Language.EN.toString().toLowerCase());
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

		Word lastVerb = new Word();
		if(!verbsInOrder.isEmpty())
			lastVerb = verbsInOrder.getLast();

		if (token.pos.startsWith("V") || token.pos.matches("MD")) 
		{
			pp = "-";	
			processVerb(token);
			if(token.tense.isEmpty())
				token.tense = "-";
			verbsInOrder.add(token);

			if(lastVerb != null && lastVerb.word.equalsIgnoreCase("did")) //in the case of the did modificator, all the sentence is in past
			{
				token.tense = lastVerb.tense;
				//token.relEvents.add(lastVerb);
			}

			lastVerb = token;

			if (token.prev != null && token.prev.pos.equalsIgnoreCase("rb") && token.prev.word.equalsIgnoreCase("not"))
			{
				if(verbsInOrder != null)
					lastVerb.polarity = "negative";
			}else
			{
				lastVerb.polarity = "positive";
			}


		}
		token.preposition = pp;


		return;
	}
	
	private void processVerb(Word token) {
		String assertype;
		String auxiliary;
		String tense = "-";
		// Freeling tenses
		    String FreelingTense = token.pos.substring(1, 4); // 0 type, 1 mode, 2 time
		    //if (FreelingTense.charAt(0) == 'M') { // only main verbs
		    if (FreelingTense.charAt(0) == 'A' || FreelingTense.charAt(0) == 'S') {
		        auxiliary = "1";
		    }
		    if (FreelingTense.charAt(1) == 'G') { // gerundio
		        if (token.prev != null && token.prev.word.matches("(?:est(?:oy|ás|á|amos|áis|án|é|és|emos|éis|én))")) {
		            tense = "present-continuous";
		        } else if(token.prev != null) {
		            if (token.prev.word.matches("(estaba(?:s|n|is)?|estábamos|estuvie(?:ra|se)(?:s|n|is)?|estuvié(?:ra|se)mos)")) {
		                tense = "past-continuous";
		            } else if(token.prev.prev != null) {
		                if (token.prev.word.equals("estado") && token.prev.prev.word.matches("(?:he|has|ha|hemos|habéis|han)")) {
		                    tense = "present-perfect-compound-continuous";
		                } else {
		                    if (token.prev.word.equals("estado") && token.prev.prev.word.matches("había(?:s|n|mos|is)?")) {
		                        tense = "past-perfect-compound-continuous";
		                    }
		                }
		            }
		        }
		    } else {
		        if (FreelingTense.charAt(1) == 'P') { // participio
		            if (token.prev != null && token.prev.word.matches("(?:he|has|ha|hemos|habéis|han)") || (token.prev.word.equals("sido") && token.prev.prev.word.matches("(?:he|has|ha|hemos|habéis|han)"))) {
		                tense = "present-perfect-compound";
		            } else if(token.prev != null && token.prev.prev != null) {
		                if (token.prev.word.matches("había(?:s|n|mos|is)?") || (token.prev.word.equals("sido") && token.prev.prev.word.matches("había(?:s|n|mos|is)?"))) {
		                    tense = "past-perfect-compound";
		                }
		                // there's another rare case (han estado siendo transportados...) but is lingusitically obscure...
		            }
		        } else {
		            if (FreelingTense.charAt(1) == 'I' || FreelingTense.charAt(1) == 'S' || FreelingTense.charAt(1) == 'M') { // INDICATIVE, SUBJUNCTIVE, IMPERATIVE ...DISCARD INFINITVE...
		                if (FreelingTense.charAt(2) == 'P') {
		                    tense = "present";
		                } else {
		                    if (FreelingTense.charAt(2) == 'I') {
		                        tense = "past-imperfect";
		                    } else {
		                        if (FreelingTense.charAt(2) == 'S') {
		                            tense = "past-perfect-simple";
		                        } else {
		                            if (FreelingTense.charAt(2) == 'F') {
		                                tense = "future";
		                            } else {
		                                if (FreelingTense.charAt(2) == 'C') {
		                                    tense = "conditional";
		                                }
		                            }
		                        }

		                    }
		                }
		            }
		            // hack for Spanish infinitives: NOT useful since we loose the tense of the sentence
		            /*else{
		                if (FreelingTense.charAt(1) == 'N'){
		                    tense = "present"; // generic infinitive verbs... (we can decide what to do with them)
		                }
		            }*/
		        }
		    }
		    //}

		    if (!tense.equals("-")) {

		        if (token.prev != null && (token.prev.word.matches("(no|nunca|jamas)") || (token.prev.word.matches("(se|me|nos|os|fu.+|he|has|ha|hemos|habéis|han|había(?:s|n|mos|is)?)")) &&
		        		(token.prev.prev.word.matches("(no|nunca|jamás)") || token.prev.prev.prev.word.matches("(no|nunca|jamás)")))) {
		            assertype = "negative";
		        } else {
		            assertype = "positive";
		        }
		    }
		token.tense = tense;
	}

}
