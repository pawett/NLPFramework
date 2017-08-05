package com.NLPFramework.TimeML.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Optional;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.EventDCTRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.EventEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.EventSubEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.FeaturesEventAnnotatedFormatter;
import com.NLPFramework.Formatters.FeaturesTimexAnnotatedFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

/**
 *
 * @author Héctor Llorens
 * @since 2011
 */
// NEEDS CORRECT BIO Element ANNOTATION PLUS, ATTRIBUTES ANNOTATED IN B...
// annotation scoring possibilities are
// ELEM correct, missing, spurious
// TEXT correct, missing, spurious, incorrect
// ATTRIBUTES over correct instances check if they are correctly classified...
//      CLASS/TYPE ATTRIBUTES WILL BE TREATED IN A SPECIAL WAY

public class Scorer {


    public Score score_class(String annot, String key, int classcol) {
        // recorrer ambdos fitxers de forma síncrona i anar calculat els scores segons el constructor
        Score score = null;
        int numline = 0;
        int classcolk=classcol;
        try {
            BufferedReader keyreader = new BufferedReader(new FileReader(key));
            BufferedReader annotreader = new BufferedReader(new FileReader(annot));

            score = new Score(key, annot);
            Judgement judgement = null;
            String keyline, annotline;



            while ((keyline = keyreader.readLine()) != null) 
            {        	
            	 if(keyline.isEmpty())
                 	continue;
                numline++;
                if ((annotline = annotreader.readLine()) == null) {
                	throw new Exception("Scored annotation ended before key annotation");
                }
                
               
                // check end of sentence and save judgements if needed

                String[] keyarr = keyline.split("\\|");
                String[] annotarr = annotline.split("\\|");

                if(classcol==-1){
                    classcol=annotarr.length - 1;
                    classcolk=keyarr.length - 1;
                }
                
                if(keyarr[classcolk].equals("-") && annotarr[classcol].equals("-"))
                	continue;
                /*if (keyarr.length < annot.getPipesDescArrCount() || annotarr.length < annot.getPipesDescArrCount()) {
                    throw new Exception("Malformed pipesFile line ("+numline+"): Has less columns ("+annotarr.length+"|"+keyarr.length+") than description file ("+annot.getPipesDescArrCount()+")");
                }*/

                // MAIN PART -------------------------------------------------------------------
                judgement = new Judgement(JudgementType.corr, "class", "", numline, keyline, annotline);
                if(!annotarr[classcol].equals(keyarr[classcolk])){
                    judgement.changeJudgement(JudgementType.inco);
                    score.addfn("class");
                    score.addfp("class");
                }else{
                    score.addtp("class");
                }
                score.add(judgement);
                judgement = null;
            }
            if ((annotline = annotreader.readLine()) != null) {
                throw new Exception("Key annotation ended before scored annotation");
            }
        } catch (Exception e) {
            System.err.println("\nErrors found (" + this.getClass().getSimpleName() + "):\n\t" + e.toString() + " (line " + numline + ")\n");
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
                e.printStackTrace(System.err);
                System.exit(1);
            }
            return null;
        }
        return score;

    }


    public void compare_scores(Score improved, Score base){
        Scomp scomp=new Scomp(improved,base);
        scomp.print();

    }

    public Score score(String elem, String type, TokenizedFileHashtable annotatedTestFiles, TokenizedFileHashtable keyFiles, String filter) 
    {
    	Score score = new Score(elem + "-" + type, "all");
    	for(String fileName : keyFiles.keySet())
		{	
    		TokenizedFile annotatedFile = annotatedTestFiles.get(fileName);
    		TokenizedFile keyFile = keyFiles.get(fileName);
    		if(type.equalsIgnoreCase("recognition"))
    			scoreRecognition(elem, filter, score, annotatedFile, keyFile);
    		else if(type.equalsIgnoreCase("classification"))
    			scoreClassification(elem, filter, score, annotatedFile, keyFile);
    		else if(elem.equalsIgnoreCase("tlink"))
    			scoreTimeLinkClassification(score, (TimeMLFile)annotatedFile, (TimeMLFile)keyFile);
    		else if(elem.equalsIgnoreCase("e-dct"))
    			scoreTimeLinkDCTClassification(score, (TimeMLFile)annotatedFile, (TimeMLFile)keyFile);
    		else if(elem.equalsIgnoreCase("tlink-event"))
    			scoreTimeLinkEventEventClassification(score, (TimeMLFile)annotatedFile, (TimeMLFile)keyFile);
    		else if(elem.equalsIgnoreCase("tlink-subevent"))
    			scoreTimeLinkEventSubEventClassification(score, (TimeMLFile)annotatedFile, (TimeMLFile)keyFile);
    			
		}
		return score;
	}

	public void scoreRecognition(String elem, String filter, Score score, TokenizedFile annotatedFile,
			TokenizedFile keyFile) {
		int sentenceNum = 0;
		for(TokenizedSentence keySentence : keyFile)
		{
			TokenizedSentence annotatedSentence = annotatedFile.get(sentenceNum);
			try
			{
				if(elem.equalsIgnoreCase("event"))
					compareEventSentences(keySentence, annotatedSentence, score, filter);
				else if(elem.equalsIgnoreCase("timex"))
					compareSentencesTimex(keySentence, annotatedSentence, score, filter);
				sentenceNum++;
				
			}catch(Exception ex)
			{
				Logger.WriteError("Error processing event", ex);
			}
		}
	}
	
	public void scoreTimeLinkClassification(Score score, TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		LinkedList<Annotation> timeLinks = keyFile.annotations.get(TimeLink.class);
		if(timeLinks == null)
			return;
		for(Annotation tlObject : timeLinks)
		{
			
			TimeLink keyTL = (TimeLink) tlObject;
			
			MakeInstance mkInstance = keyTL.relatedToEventInstance != null ? keyTL.relatedToEventInstance : keyTL.eventInstance;
			Timex3 timex = keyTL.relatedToTime;
			
			if(mkInstance == null || timex == null || timex.id.equals("0") || timex.id.equals("t0"))// || TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type).equals(TimeLinkRelationType.INCLUDES))// || timex.id.equals("t0"))// || TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type).equals(TimeLinkRelationType.INCLUDES))
				continue;
			if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber != (timex.word.sentenceNumber))
				continue;

			String syntRelation = "sentence";
			if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber == (timex.word.sentenceNumber) && mkInstance.event.word.phra_id.equals(timex.word.phra_id))
				syntRelation = "sub-sent";
			
			if(mkInstance.event.word != null && timex != null && timex.word != null && mkInstance.event.word.sentenceNumber == (timex.word.sentenceNumber) && mkInstance.event.word.mainphraseIOB.equals(timex.word.mainphraseIOB))
				syntRelation = "phrase";
			//The filter must be: if event is main verb and timex in a subordinate sentence or clause
			// and both event and timex are in the same clause
			
			//if(syntRelation.equals("sentence"))
			//	continue;
			
			boolean relationIsReverse = false;
			if(keyTL.relatedToEventInstance != null)
				relationIsReverse = true;
			
			TokenizedSentence sentence = keyFile.get(mkInstance.event.word.sentenceNumber);
			Word eventDepVerb = sentence.getWordDependantVerb(mkInstance.event.word);
			Word timexDepVerb = sentence.getWordDependantVerb(timex.word);
			Word sentenceMainVerb = TimeMLHelper.getSentenceMainEvent(sentence);	
			
			//if(sentenceMainVerb != null && eventDepVerb != null && !sentenceMainVerb.equals(eventDepVerb) && !TimeMLHelper.areWordsInSameClause(keyFile, mkInstance.event.word, timex.word))
			//	continue;
			
			EventTimexRelationAnnotatedFormatter formatter = new EventTimexRelationAnnotatedFormatter();
			
			
			Optional<TimeLink> annotatedTL = annotatedFile.getTimeLinks().stream().filter(tl -> tl.id.equalsIgnoreCase(keyTL.id)).findFirst();
			if(annotatedTL.isPresent())
			{
				Judgement judgement = getJudgement("tlink-event-timex", JudgementType.inco, formatter.annotateTimeLink(keyFile, tlObject), formatter.annotateTimeLink(annotatedFile, annotatedTL.get()));
				if(TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) != null && TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false).equals(TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, relationIsReverse)))
					judgement.changeJudgement(JudgementType.corr);

				Word eventWord = annotatedTL.get().eventInstance != null ? annotatedTL.get().eventInstance.event.word : annotatedTL.get().relatedToEventInstance.event.word;
				TokenizedSentence s = keyFile.get(eventWord.sentenceNumber);
				Logger.WriteDebug(judgement.getJudgement_str() + " classification. Expected :" + TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, relationIsReverse) + " Obtained: " +TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) );
				if(judgement.getJudgement_str().equals("inco"))
				{
					Logger.WriteDebug(judgement.getKeylines());
					Logger.WriteDebug(s.toStringSyntFlat());
				}
				
				
				score.add(judgement);
			}
			
			
		}
	}
	
	public void scoreTimeLinkDCTClassification(Score score, TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		LinkedList<Annotation> timeLinks = keyFile.annotations.get(TimeLink.class);
		if(timeLinks == null)
			return;
		for(Annotation tlObject : timeLinks)
		{
			
			TimeLink keyTL = (TimeLink) tlObject;
			
			MakeInstance mkInstance = keyTL.relatedToEventInstance != null ? keyTL.relatedToEventInstance : keyTL.eventInstance;
			Timex3 timex = keyTL.relatedToTime;
			
			if(mkInstance == null || timex == null || !(timex.id.equals("0") || timex.id.equals("t0")))// || TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type).equals(TimeLinkRelationType.INCLUDES))// || timex.id.equals("t0"))// || TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type).equals(TimeLinkRelationType.INCLUDES))
				continue;
			
			boolean relationIsReverse = false;
			if(keyTL.relatedToEventInstance != null)
				relationIsReverse = true;
			
			EventDCTRelationAnnotatedFormatter formatter = new EventDCTRelationAnnotatedFormatter();
			
			
			Optional<TimeLink> annotatedTL = annotatedFile.getTimeLinks().stream().filter(tl -> tl.id.equalsIgnoreCase(keyTL.id)).findFirst();
			if(annotatedTL.isPresent())
			{
				Judgement judgement = getJudgement("tlink-event-dct", JudgementType.inco, formatter.annotateTimeLink(keyFile, tlObject), formatter.annotateTimeLink(annotatedFile, annotatedTL.get()));
				if(TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) != null && TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false).equals(TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, relationIsReverse)))
					judgement.changeJudgement(JudgementType.corr);
				
				Word eventWord = annotatedTL.get().eventInstance != null ? annotatedTL.get().eventInstance.event.word : annotatedTL.get().relatedToEventInstance.event.word;
				TokenizedSentence s = keyFile.get(eventWord.sentenceNumber);
				Logger.WriteDebug(judgement.getJudgement_str() + " classification. Expected :" + TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, relationIsReverse) + " Obtained: " +TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) );
				if(judgement.getJudgement_str().equals("inco"))
				{
					Logger.WriteDebug(judgement.getKeylines());
					Logger.WriteDebug(s.toStringSyntFlat());
				}
				score.add(judgement);
			}
			
			
		}
	}
	
	
	public void scoreTimeLinkEventEventClassification(Score score, TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		LinkedList<Annotation> timeLinks = keyFile.annotations.get(TimeLink.class);
		if(timeLinks == null)
			return;
		for(Annotation tlObject : timeLinks)
		{
			
			TimeLink keyTL = (TimeLink) tlObject;
			
			MakeInstance mkInstance = keyTL.eventInstance;
			MakeInstance mkRelatedInstance = keyTL.relatedToEventInstance;
			
			if(mkInstance == null || mkRelatedInstance == null || TimeMLHelper.areWordsInSameSentence(keyFile, mkInstance.event.word, mkRelatedInstance.event.word))
				continue;
		
			EventEventRelationAnnotatedFormatter formatter = new EventEventRelationAnnotatedFormatter();
			
			
			Optional<TimeLink> annotatedTL = annotatedFile.getTimeLinks().stream().filter(tl -> tl.id.equalsIgnoreCase(keyTL.id)).findFirst();
			if(annotatedTL.isPresent())
			{
				Judgement judgement = getJudgement("tlink-event-event", JudgementType.inco, formatter.annotateTimeLink(keyFile, tlObject), formatter.annotateTimeLink(annotatedFile, annotatedTL.get()));
				if(TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) != null && TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false).equals(TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, false)))
					judgement.changeJudgement(JudgementType.corr);
				
				Logger.WriteDebug(judgement.getJudgement_str() + " classification. Expected :" + TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, false) + " Obtained: " +TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) );
				
				score.add(judgement);
			}
			
			
		}
	}
	
	public void scoreTimeLinkEventSubEventClassification(Score score, TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		LinkedList<Annotation> timeLinks = keyFile.annotations.get(TimeLink.class);
		if(timeLinks == null)
			return;
		for(Annotation tlObject : timeLinks)
		{
			
			TimeLink keyTL = (TimeLink) tlObject;
			
			MakeInstance mkInstance = keyTL.eventInstance;
			MakeInstance mkRelatedInstance = keyTL.relatedToEventInstance;
			
			if(mkInstance == null || mkRelatedInstance == null || mkInstance.event.word.sentenceNumber != mkRelatedInstance.event.word.sentenceNumber)
				continue;
			
			TokenizedSentence sentence = keyFile.get( mkInstance.event.word.sentenceNumber);
			Word mainVerb = TimeMLHelper.getSentenceMainEvent(sentence);
			
			if(mkInstance.event.word.isVerb == true && mkRelatedInstance.event.word.isVerb == true &&  !mkInstance.event.word.equals(mainVerb) &&  !mkRelatedInstance.event.word.equals(mainVerb))
				continue;
		
			EventSubEventRelationAnnotatedFormatter formatter = new EventSubEventRelationAnnotatedFormatter();
			
			
			Optional<TimeLink> annotatedTL = annotatedFile.getTimeLinks().stream().filter(tl -> tl.id.equalsIgnoreCase(keyTL.id)).findFirst();
			if(annotatedTL.isPresent())
			{
				Judgement judgement = getJudgement("tlink-event-subevent", JudgementType.inco, formatter.annotateTimeLink(keyFile, tlObject), formatter.annotateTimeLink(annotatedFile, annotatedTL.get()));
				if(TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) != null && TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false).equals(TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, false)))
					judgement.changeJudgement(JudgementType.corr);
				
				Logger.WriteDebug(judgement.getJudgement_str() + " classification. Expected :" + TimeMLHelper.getTimeLinkRelationTypeSimplified(keyTL.type, false) + " Obtained: " +TimeMLHelper.getTimeLinkRelationTypeSimplified(annotatedTL.get().type, false) );
				
				score.add(judgement);
			}
			
			
		}
	}
	
	
	public void scoreClassification(String elem, String filter, Score score, TokenizedFile annotatedFile,
			TokenizedFile keyFile) {
		int sentenceNum = 0;
		
			
		for(TokenizedSentence keySentence : keyFile)
		{
			TokenizedSentence annotatedSentence = annotatedFile.get(sentenceNum);
			try
			{
				switch(elem.toLowerCase())
				{
					case "event":
						compareEventClassificationSentences(keySentence, annotatedSentence, score, filter);
						break;
					case "timex":
						compareTimexClassificationSentences(keySentence, annotatedSentence, score, filter);
						break;
					
				}
				
					
				sentenceNum++;
				
			}catch(Exception ex)
			{
				Logger.WriteError("Error processing event", ex);
			}
		}
	}
    
   

	private Judgement getJudgement(String elem, JudgementType type,TokenizedSentence keySentence, Word keyWord, TokenizedSentence annotatedSentence, Word annotatedWord)
    {
    	ISentenceFormatter formatter = null;
    	Judgement j = null;
    	
    	if(elem.equalsIgnoreCase("event"))
    		formatter = new FeaturesEventAnnotatedFormatter();
    	else if (elem.equalsIgnoreCase("timex"))
    		formatter = new FeaturesTimexAnnotatedFormatter();
    	
    	if(formatter != null)
    		j = new Judgement(type, elem, "", keyWord.sentencePosition, formatter.toString(keySentence, keyWord), formatter.toString(annotatedSentence, annotatedWord));
    	else
    		j = new Judgement(type, elem, "", 0, "", "");
    	//j.extendJudgement(annotatedFormatter.toString(keySentence), annotatedFormatter.toString(keySentence));
    	return j;
    }
	
	private Judgement getJudgement(String elem, JudgementType type, String keyValue, String annotatedValue)
    {
     	Judgement j = null;
  
    	j = new Judgement(type, elem, "", 0, keyValue, annotatedValue);
     	//j = new Judgement(type, elem, "", 0, "", "");
    	return j;
    }
    
    private void compareEventSentences(TokenizedSentence keySentence, TokenizedSentence annotatedSentence, Score score, String filter) throws Exception 
    {
    	EntityMapper<Annotation> annotatedMap = null;
    	EntityMapper<Annotation> keyMap = null;
    	Judgement judgement = null;
    	ISentenceFormatter formatter =  new FeaturesEventAnnotatedFormatter();
    	
    	for(Word keyWord : keySentence)
    	{
    		if(keyMap != null && keyWord.sentencePosition > keyMap.endWordPosition)
    			keyMap = null;
    		if(annotatedMap != null && keyWord.sentencePosition > annotatedMap.endWordPosition)
    			annotatedMap = null;
    		   		
    		if(annotatedSentence.size() <= keyWord.sentencePosition)
    		{
    			Logger.WriteDebug("Annotated Sentence is smaller than the key one. Annotated:" + annotatedSentence + " Key: "+keySentence);
    			continue;
    		}
    		Word annotatedWord = annotatedSentence.get(keyWord.sentencePosition);
    		
    		if(keyMap == null && annotatedMap == null && judgement != null)
    		{
    			score.add(judgement);
    			judgement = null;
    		}
    		
    		if(keySentence.annotations.get(Event.class) != null && keySentence.annotations.get(Event.class).get(keyWord) != null)
    		{
    			if(filter == null || keyWord.pos.contains(filter))
    			{
    				if(keyMap != null)
    					throw new Exception("Overlapped key events");

    				keyMap = keySentence.annotations.get(Event.class).get(keyWord);
    				if(judgement == null)
    					judgement = getJudgement("event", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
    			}
    		}
    		
    		
    		if(annotatedSentence.annotations.get(Event.class) != null && annotatedSentence.annotations.get(Event.class).get(annotatedWord) != null)
    		{
    			if(filter == null || keyWord.pos.contains(filter))
    			{
    				if(annotatedMap != null)
    					throw new Exception("Overlapped annotated events");

    				annotatedMap = annotatedSentence.annotations.get(Event.class).get(annotatedWord);

    				if(judgement == null)
    					judgement = getJudgement("event", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
    			}
    		}
    		
    		
    		if(keyMap != null && annotatedMap != null)
    		{
    			if(keyMap.firstWordPosition == annotatedMap.firstWordPosition 
    					&& keyMap.endWordPosition == annotatedMap.endWordPosition)
    				judgement.changeJudgement(JudgementType.corr);
    			else
    				judgement.changeJudgement(JudgementType.spur);
    		}
    		
    		if(keyMap != null && annotatedMap == null)
    			judgement.changeJudgement(JudgementType.miss);
    		
    		if(keyMap == null && annotatedMap != null)
    			judgement.changeJudgement(JudgementType.inco);
    		
    		if((keyMap != null && keyWord.sentencePosition != keyMap.firstWordPosition)
    				|| (annotatedMap != null && keyWord.sentencePosition != annotatedMap.firstWordPosition))
    			judgement.extendJudgement(keyWord.word, annotatedWord.word);
    		
    	}
    	
	}
    
    
    private void compareEventClassificationSentences(TokenizedSentence keySentence, TokenizedSentence annotatedSentence, Score score, String filter) throws Exception 
    {
    	EntityMapper<Annotation> annotatedMap = null;
    	EntityMapper<Annotation> keyMap = null;
    	Judgement judgement = null;
    	if(keySentence.annotations.get(Event.class) != null)
    	{
	    	for(Word keyWord : keySentence.annotations.get(Event.class).keySet())
	    	{
	    		Word annotatedWord = annotatedSentence.get(keyWord.sentencePosition);
	    		annotatedMap = annotatedSentence.annotations.get(Event.class).get(annotatedWord);
	    		keyMap = keySentence.annotations.get(Event.class).get(keyWord);
	    		Event annotatedEvent = (Event)annotatedMap.element;
	    		Event keyEvent = (Event)keyMap.element;
	    		judgement = getJudgement("event", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
	    		if(annotatedEvent.eventClass.equals(keyEvent.eventClass))
	    			judgement.changeJudgement(JudgementType.corr);
	    		if(filter == null || keyWord.pos.contains(filter))
	    			score.add(judgement);
	    	}
    	}
    	
	}
    
    
    private void compareSentencesTimex(TokenizedSentence keySentence, TokenizedSentence annotatedSentence, Score score, String filter) throws Exception 
    {
    	EntityMapper<Annotation> annotatedMap = null;
    	EntityMapper<Annotation> keyMap = null;
    	Judgement judgement = null;
    	
    	ISentenceFormatter formatter = null;
    	
    	formatter = new FeaturesTimexAnnotatedFormatter();
    
    	
    	for(Word keyWord : keySentence)
    	{
    		if(keyMap != null && keyWord.sentencePosition > keyMap.endWordPosition)
    			keyMap = null;
    		if(annotatedMap != null && keyWord.sentencePosition > annotatedMap.endWordPosition)
    			annotatedMap = null;
    		   		
    		Word annotatedWord = annotatedSentence.get(keyWord.sentencePosition);
    		
    		if(keyMap == null && annotatedMap == null && judgement != null)
    		{
    			score.add(judgement);
    			judgement = null;
    		}
    		
    		if(keySentence.annotations.get(Timex3.class)!= null && keySentence.annotations.get(Timex3.class).get(keyWord) != null)
    		{
    			if(filter == null || keyWord.pos.matches(filter))
    			{
    				if(keyMap != null)
    					throw new Exception("Overlapped key timex");

    				keyMap = keySentence.annotations.get(Timex3.class).get(keyWord);
    				if(judgement == null)
    					judgement = getJudgement("timex", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
    			}
    		}
    		
    		
    		if(keySentence.annotations.get(Timex3.class) != null && annotatedSentence.annotations.get(Timex3.class).get(annotatedWord) != null)
    		{
    			if(filter == null || keyWord.pos.matches(filter))
    			{
    				if(annotatedMap != null)
    					throw new Exception("Overlapped annotated timex");

    				annotatedMap = annotatedSentence.annotations.get(Timex3.class).get(annotatedWord);

    				if(judgement == null)
    					judgement = getJudgement("timex", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
    			}
    		}
    		
    		
    		if(keyMap != null && annotatedMap != null)
    		{
    			if(keyMap.firstWordPosition == annotatedMap.firstWordPosition 
    					&& keyMap.endWordPosition == annotatedMap.endWordPosition)
    				judgement.changeJudgement(JudgementType.corr);
    			else
    				judgement.changeJudgement(JudgementType.spur);
    		}
    		
    		if(keyMap != null && annotatedMap == null)
    			judgement.changeJudgement(JudgementType.miss);
    		
    		if(keyMap == null && annotatedMap != null)
    			judgement.changeJudgement(JudgementType.inco);
    		
    		if((keyMap != null && keyWord.sentencePosition != keyMap.firstWordPosition) || (annotatedMap != null && keyWord.sentencePosition != annotatedMap.firstWordPosition)) 				
    				judgement.extendJudgement(formatter.toString(keySentence, keyWord), formatter.toString(annotatedSentence, annotatedWord));	
    	}
    	
	}

    private void compareTimexClassificationSentences(TokenizedSentence keySentence, TokenizedSentence annotatedSentence, Score score, String filter) throws Exception 
    {
    	EntityMapper<Annotation> annotatedMap = null;
    	EntityMapper<Annotation> keyMap = null;
    	Judgement judgement = null;
    	
    	if(annotatedSentence.annotations.get(Timex3.class) != null)
    	{
    	
	    	for(Word keyWord : keySentence.annotations.get(Timex3.class).keySet())
	    	{
	    		Word annotatedWord = annotatedSentence.get(keyWord.sentencePosition);
	    		if(annotatedSentence.annotations.get(Timex3.class) == null)
	    		{
	    			judgement = getJudgement("timex", JudgementType.miss, keySentence, keyWord, annotatedSentence, annotatedWord);
	    			score.add(judgement);
	    			continue;
	    		}
	    			
	    		annotatedMap = annotatedSentence.annotations.get(Timex3.class).get(annotatedWord);
	    		keyMap = keySentence.annotations.get(Timex3.class).get(keyWord);
	    		Timex3 annotatedTimex = (Timex3)annotatedMap.element;
	    		Timex3 keyEvent = (Timex3) keyMap.element;
	    		judgement = getJudgement("timex", JudgementType.inco, keySentence, keyWord, annotatedSentence, annotatedWord);
	    		if(annotatedTimex.type.equals(keyEvent.type))
	    			judgement.changeJudgement(JudgementType.corr);
	    		
	    		score.add(judgement);
	    	}
    	}
    	
	}
    
    

	private boolean areWordsEquals(Word a, Word b)
    {
    	return a.sentenceNumber == b.sentenceNumber &&
    			a.sentencePosition == b.sentencePosition &&
    			a.word.equalsIgnoreCase(b.word);
    }


}
