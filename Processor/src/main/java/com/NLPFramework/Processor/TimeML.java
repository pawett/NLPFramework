package com.NLPFramework.Processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.xml.parsers.ParserConfigurationException;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.PropBankArgument;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Files.TimeMLDocument;
import com.NLPFramework.Formatters.ITLinkFormatter;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

public class TimeML {


	private TimeMLDocument timeMLDocument;
	private TimeMLFile features;
	
	public TimeML(TimeMLFile document)
	{
		features = document;

		Event mainEvent = null;

		timeMLDocument = new TimeMLDocument(document.getDCT().value);
		

		try {		

			//Word govEvent = null;
			int timeId = 1;
			int eventId = 1;
			Word previousSentenceMainEvent = null;
			Word currentSentenceMainEvent = null;
			Word currentSentenceTime = null;
			
			
			/*for(TokenizedSentence sentence : features)
			{	
				for(Word time : sentence.annotations.get(Timex3.class).keySet())
				{
					Timex3 timex3 = (Timex3)sentence.annotations.get(Timex3.class).get(time).element;
					timex3.id = "t" + timeId;
					timeId++;
				}
				
				
				//processMakeInstances(sentence);
				//currentSentenceMainEvent = TimeMLHelper.getSentenceMainEvent(sentence);
				
				/*for(Word token : sentence)
				{
					EntityMapper<Annotation> event = sentence.annotations.get(Event.class).get(token);
					if(event != null)
					{
						
						token.id = "e" + eventId;
						eventId++;
						/*if(token.event_IOB2.startsWith("B"))
						{
							sentence.events.put(token, new ArrayList<Word>());
							sentence.events.get(token).add(token);
							currentSentenceMainEvent = token;
						}else
						{
							sentence.events.get(currentSentenceMainEvent).add(token);
						}*/
				/*		currentSentenceMainEvent = token;
					}
					if(token.element_type.equalsIgnoreCase("timex"))
					{

						token.id = "t" +timeId;
						timeId++;
						/*if(token.time_IOB2.startsWith("B"))
						{
							sentence.times.put(token, new ArrayList<Word>());
							sentence.times.get(token).add(token);
							currentSentenceTime = token;
						}else
						{
							Logger.WriteDebug(sentence + " : " + token + " Time: "+ currentSentenceTime);
							sentence.times.get(currentSentenceTime).add(token);
						}*/

					/*}*/
				//}
		/*		
				processInterSententialEvents(previousSentenceMainEvent, currentSentenceMainEvent);
				processSentenceEventsAndTimes(sentence);
				previousSentenceMainEvent = currentSentenceMainEvent;
				currentSentenceMainEvent = null;
			}*/

		} catch (Exception e) {
			Logger.WriteError("Errors found (TempEval):", e);
			System.exit(1);  
		}
	}

	private void processMakeInstances(TokenizedSentence sentence) 
	{
		if(sentence.annotations.get(Event.class) == null)
			return;
		for(Word key : sentence.annotations.get(Event.class).keySet())
		{
			MakeInstance mkCurrent = new MakeInstance(key, (Event)sentence.annotations.get(Event.class).get(key).element);
			
			timeMLDocument.makeInstances.add(mkCurrent);
			features.addAnnotation(MakeInstance.class, mkCurrent);
		}
	}

	private MakeInstance getMakeInstanceFromTimeMLDocument(Word w)
	{
		Optional<MakeInstance> mkPrevious = timeMLDocument.makeInstances.stream()
				.filter(m -> m.event.word.equals(w))
				.findFirst();
		if(mkPrevious.isPresent())
			return mkPrevious.get();

		return null;
	}

	private Timex3 getTimexFromTimeMLDocument(Word w)
	{
		Optional<Timex3> timex = timeMLDocument.timex3s.stream()
				.filter(t -> t.word.equals(w))
				.findFirst();
		if(timex.isPresent())
			return timex.get();

		return null;
	}

	/*private void processInterSententialEvents(Word previousSentenceMainEvent, Word currentSentenceMainEvent) 
	{
		if(previousSentenceMainEvent != null && currentSentenceMainEvent != null)
		{

			MakeInstance mkPrevious = getMakeInstanceFromTimeMLDocument(previousSentenceMainEvent);

			MakeInstance mkCurrent = getMakeInstanceFromTimeMLDocument(currentSentenceMainEvent);
			if(mkPrevious == null)
			{
				mkPrevious = new MakeInstance(previousSentenceMainEvent, new Event(previousSentenceMainEvent));
			}

			if(mkCurrent == null)
			{
				mkCurrent = new MakeInstance(currentSentenceMainEvent, new Event(currentSentenceMainEvent));
			}

			TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
			tLink.eventInstance = mkPrevious;
			tLink.relatedToEventInstance = mkCurrent;
			timeMLDocument.links.add(tLink);
			timeMLDocument.eEventLinks.add(tLink);
			features.addAnnotation(TimeLink.class, tLink);
			
		}

	}


	private void processSentenceEventsAndTimes(TokenizedSentence sentence) {
		// TODO Auto-generated method stub
		ArrayList<MakeInstance> intraSentential = new ArrayList<>();
		Timex3 relatedTime = timeMLDocument.DCT;
		Enumeration<Word> times = sentence.annotations.get(Timex3.class).keys();

		while(times.hasMoreElements())
		{
			Word time = times.nextElement();
			
			Timex3 timex3 = getTimexFromTimeMLDocument(time);

			if(timex3 == null)
			{
				timex3 = new Timex3(time);
				timex3.anchorTimeID = relatedTime.id;
				relatedTime = timex3;
				timeMLDocument.timex3s.add(timex3);
				
			}	
		}

		Enumeration<Word> events = sentence.annotations.get(Event.class) != null ? sentence.annotations.get(Event.class).keys() : null;

		while(events != null && events.hasMoreElements())
		{
			Word event = events.nextElement();
			MakeInstance mk = getMakeInstanceFromTimeMLDocument(event);
			if(mk == null)
			{
				Event domainEvent = new Event(event);
				mk = new MakeInstance(event, domainEvent);
				timeMLDocument.events.add(domainEvent);
			}

			timeMLDocument.makeInstances.add(mk);

			if(sentence.annotations.get(Timex3.class).keySet().size() >= 1)
			{
				//Event-timex
				Enumeration<Word> times2 = sentence.annotations.get(Timex3.class).keys();

				while(times2.hasMoreElements())
				{
					Word time = times2.nextElement();
					Timex3 timex3 = getTimexFromTimeMLDocument(time);

					TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
					tLink.eventInstance = mk;
					tLink.relatedToTime = timex3;
					timeMLDocument.links.add(tLink);
					timeMLDocument.eTimexLinks.add(tLink);
					features.addAnnotation(TimeLink.class, tLink);
				}
			}

			//sub_event-event
			if(intraSentential.size() > 0)
			{
				for(MakeInstance prevInstance : intraSentential)
				{
					TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
					tLink.eventInstance = prevInstance;
					tLink.relatedToEventInstance = mk;
					timeMLDocument.links.add(tLink);
					timeMLDocument.subEventEventLinks.add(tLink);
					features.addAnnotation(TimeLink.class, tLink);
				}
			}

			//Event-DCT

			TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
			tLink.eventInstance = mk;
			tLink.relatedToTime = timeMLDocument.DCT;
			timeMLDocument.links.add(tLink);
			timeMLDocument.eDctLinks.add(tLink);
			features.addAnnotation(TimeLink.class, tLink);

		}		
	}

/*
	public void updateEventTimexFromFileReader(String filePath)
	{
		updateFromFileReader(filePath, timeMLDocument.eTimexLinks, new TempEvalEventTimeFormatter());
	}

	public void updateEventDCTFromFileReader(String filePath)
	{
		updateFromFileReader(filePath, timeMLDocument.eDctLinks, new TempEvalEventDCTFormatter());
	}

	public void updateMainEventEventFromFileReader(String filePath)
	{
		updateFromFileReader(filePath, timeMLDocument.eEventLinks, new TempEvalMainEventEventFormatter());
	}

	public void updateSubEventEventFromFileReader(String filePath)
	{
		updateFromFileReader(filePath, timeMLDocument.subEventEventLinks, new TempEvalSubEventEventFormatter());
	}
*/
	public void updateFromFileReader(String filePath, List<TimeLink> links, ITLinkFormatter formatter)
	{
		BufferedReader fileReader = null;
		try {
			fileReader = new BufferedReader(new FileReader(filePath));
			
			String line="";

			for(TimeLink link : links)
			{
				line = fileReader.readLine();
				if (line == null )return;
				if(line.isEmpty()) continue;
				formatter.setValues(link, line);

			} 	        	

		}catch(Exception ex)
		{
			Logger.WriteError("Error updating TimeML file", ex);
		}
		finally 
		{
			if (fileReader != null) 
			{
				try {
					fileReader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Logger.WriteError("Error closing fileReader in TimeML file", e);
				}
			}

		}
		
	}

	/*public File toFileEventTimex(String path)
	{
		return toFile(path, timeMLDocument.eTimexLinks, new TempEvalEventTimeFormatter());
	}

	public File toFileEventDCT(String path)
	{
		return toFile(path, timeMLDocument.eDctLinks, new TempEvalEventDCTFormatter());
	}

	public File toFileMainEventEvent(String path)
	{
		return toFile(path, timeMLDocument.eEventLinks, new TempEvalMainEventEventFormatter());
	}

	public File toFileSubEventEvent(String path)
	{
		return toFile(path, timeMLDocument.subEventEventLinks, new TempEvalSubEventEventFormatter());
	}
*/
	public File toFile(String path, List<TimeLink> links, ITLinkFormatter formatter)
	{
		String fileName = features.getName() + "." + formatter.getClass().getName();
		if(path != null)
			fileName = path + File.separator + fileName;

		File f = new File(fileName);
		BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

			try{
				StringBuilder sb = new StringBuilder();
				for(TimeLink link : links)
				{
					sb.append(features.getName());
					sb.append(formatter.toString(link));
					sb.append(System.lineSeparator());
				}

				output.write(sb.toString());
			} catch (IOException e) {
				Logger.WriteError("Cannot write in the file" + fileName, e);
			} finally {

				if (output != null)
				{
					output.close();
				}
			}
		} catch (IOException e1) {
			Logger.WriteError("Cannot create the file in the path" + fileName, e1);
		}
		return f;

	}


	public String toTML(TimeMLFile file) throws ParserConfigurationException, IOException
	{
		/*DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
	DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

	// root elements
	org.w3c.dom.Document doc = docBuilder.newDocument();
	doc*/



		File f = new File(features.getName() + ".tml");
		BufferedWriter output;
		output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));

		try{

			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" ?>");
			sb.append(System.lineSeparator());
			sb.append("<TimeML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://timeml.org/timeMLdocs/TimeML_1.2.1.xsd\">");
			sb.append(System.lineSeparator());
			sb.append("<DOCID>" + features.getName() + "</DOCID>");
			sb.append(System.lineSeparator());
			if (features.getDCT() != null) 
			{
				sb.append("<DCT><TIMEX3 tid=\"" + timeMLDocument.DCT.id + "\" type=\"DATE\" value=\"" + timeMLDocument.DCT.value + "\" temporalFunction=\"false\" functionInDocument=\"CREATION_TIME\">" +timeMLDocument.DCT.value + "</TIMEX3></DCT>");
				sb.append(System.lineSeparator());
			}

			sb.append("<TEXT>");
			sb.append(System.lineSeparator());

			for(TokenizedSentence sentence : file)
			{
				Event mainEvent = null;
				Timex3 mainTime = null;
				EntityMapper<Annotation> timexMap = null;
				String bufferWords = "";
				for(Word word : sentence)
				{

					//Event event = TimeMLHelper.getMakeInstanceFromFile(file, word).event;
					Timex3 timex = TimeMLHelper.getTimexFromFile(file, word);

					if(timex != null)					
					timexMap = sentence.annotations.get(timex.getClass()).get(word);
					EntityMapper<Annotation> eventMap = sentence.annotations.get(Event.class) != null ? sentence.annotations.get(Event.class).get(word) : null;

					if(eventMap != null)
						sb.append(printEvent((Event)eventMap.element, ((Event)eventMap.element).word.word));
					
					
					if(timexMap == null && timex != null)
						timexMap = sentence.annotations.get(timex.getClass()).get(word);
					
					if(eventMap == null && timexMap == null)
						sb.append(" "+word.word);
					
					if(timexMap != null)
					{
						bufferWords += " " + word.word;
						if(timexMap.endWordPosition == word.sentencePosition)
						{
							sb.append(printTimex(((Timex3)timexMap.element), bufferWords));
							timexMap = null;
							bufferWords = "";
						}	
					}					
				}
				sb.append(System.lineSeparator());
			}

			sb.append("</TEXT>");

			for(Annotation mk : file.annotations.get(MakeInstance.class))
			{
				sb.append(printMakeInstances((MakeInstance)mk));
				sb.append(System.lineSeparator());
			}


			for(Annotation tl : file.annotations.get(TimeLink.class))
			{
				sb.append(printTimeLink((TimeLink)tl));
				sb.append(System.lineSeparator());
			}

			sb.append("</TimeML>");

			output.write(sb.toString());
		} catch (IOException e) {
			Logger.WriteError("Cannot write in the file", e);
		} finally {

			if (output != null)
			{
				output.close();
			}
		}
		
		
		
		return f.getAbsolutePath();

	}

	private String printTimeLink(TimeLink tl)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<TLINK lid=\"l"+tl.id+"\" relType=\"" + tl.type.toString() +"\" eventInstanceID=\"e" + tl.eventInstance.id  +"\" ");
		if(tl.relatedToTime != null)
			sb.append("relatedToTime=\"t" + tl.relatedToTime.id + "\" />");
		if(tl.relatedToEventInstance != null)
			sb.append("relatedToEventInstance=\"e" + tl.relatedToEventInstance.id +"\" />");

		return sb.toString();
	}

	private String printMakeInstances(MakeInstance mk)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<MAKEINSTANCE eiid=\"ei"+ mk.id + "\" eventID=\"e" + mk.event.id + "\" pos=\"" + mk.pos.toString() + "\" tense=\"" + mk.tense.toString() +"\" aspect=\"" + mk.aspect.toString() +"\" polarity=\"" + mk.polarity.toString() +"\"");

		if(mk.modality != null && !mk.modality.isEmpty())
		{
			sb.append(" modality=\"" + mk.modality + "\"");
		}

		sb.append("/>");

		return sb.toString();
	}

	private String printTimex(Timex3 timex3, String phrase) {
		return "<TIMEX3 tid=\"t" + timex3.id + "\" type=\"" + timex3.type.toString() +
				"\" value=\"" + timex3.value + "\" temporalFunction=\"" + timex3.temporalFunction +
				"\" functionInDocument=\"" + timex3.functionInDocument + "\" anchorTimeID=\"t" +
				timex3.anchorTimeID +"\">" + phrase + "</TIMEX3>";
	}

	private String printEvent(Event event, String phrase) {
		return "<EVENT eid=\"e" + event.id + "\" class=\"" + event.eventClass.toString() +
				"\">"+ phrase + "</EVENT>"; 
	}

}

