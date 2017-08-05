package com.NLPFramework.Processor;

import java.util.ArrayList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.Timex3;

public class Classification {

	  public static TokenizedFile setClassikEvents(TokenizedFile file) 
	    {
	    	
	    	try 
	    	{	

	    		file.parallelStream().forEach((sentence) -> 
	    		{
	    			ArrayList<Word> event = new ArrayList<>();
	    			Word previousVerb = null;
	    			for(Word token : sentence)             
	    			{
	    				
	    				if (token.event_IOB2.matches("B-.*")) 
	    				{
	    					EntityMapper<Timex3>eMap = new EntityMapper<>();
							Timex3 timex = new Timex3(token);
							eMap.element = timex;
							eMap.firstWordPosition = token.sentencePosition;
							eMap.endWordPosition = token.sentencePosition;
	    					sentence.addAnnotation(Event.class, token, eMap);
	    					processEvent(token, previousVerb);
	    					previousVerb = token;

	    				}else if (token.event_IOB2.matches("I-.*")) 
	    				{
	    					Word temp = previousVerb;
	    					while(temp.govWord != null)
	    					{
	    						temp = temp.govWord;
	    					}
	    					temp.govWord = token;
	    					sentence.annotations.get(Event.class).get(previousVerb).endWordPosition = token.sentencePosition;
	    					event.add(token);
	    					processEvent(token, previousVerb);
	    					//ProcessIncludeTimex(previousVerb, token);
	    				}else if(previousVerb != null)//the event is processed and finished, save it
	    				{
	    					
	    					event = new ArrayList<>();
	    					previousVerb = null;
	    				}
	    			}
	    		});
	    	} catch (Exception e) 
	    	{
	    		Logger.WriteError("Errors found (CLASSIK)" ,e);

	    		System.exit(1);

	    		return null;
	    	}
	    	return file;

	    }
	  
	  private static void processEvent(Word token, Word previousVerb) 
		{
		if (!token.word.equals("")) {
				     //outfile.write(features.get("file") + "|" + features.get("sentN") + "|" + features.get("tokN") + "|" + features.get("word") + "|" + features.get("pos") + "|" + features.get("lemma") + "|" + features.get("roleconf") + "|" + features.get("simpleroles") + "|" + features.get("depverb") + "|" + features.get("tense") + "|" + features.get("polarity") + "|" + features.get("mainphrase") + "|" + features.get("PPdetail") + "|" + features.get("wn"));
			 
			         if ( previousVerb != null && previousVerb.element_type.matches("(?i)event") &&
			        		 previousVerb.pos.matches("(V.*|AUX)"))
			         {
			             token.extra1 = "|1|";
			             token.extra2 = previousVerb.lemma;
			             //(features.get("lemma"));
			             token.extra3 ="-";
			         } else {
			        	 token.extra1 = "-";
			        	 token.extra2 = "-";
			        	 token.extra3 = "-";
			        	 token.extra4 = "-";
			        	 token.extra5 = "-";
			        	 token.extra6 = "-";
			         }
			     
			     token.element_type = token.event_IOB2.substring(2);
			    token.classik = token.classik;
			 }

			token.element_type = token.event_IOB2.substring(2);//type of clasificateion (timex/event

			/* if (token.classik != null && token.classik != "-") {
			     if (token.attribs.matches(".*=.*=.*") && !token.attribs.contains(";")) {
			         tempexAttribsHash = XmlAttribs.parseXMLattrs(token.attribs);
			     } else {
			         tempexAttribsHash = XmlAttribs.parseSemiColonAttrs(token.attribs);
			     }
			     if (token.element_IOB2.matches("(?i).*timex.*")) {
			         token.classik = tempexAttribsHash.get("type");
			     } else {
			         token.classik = tempexAttribsHash.get("class");
			     }
			 }*/
			
		}
}
