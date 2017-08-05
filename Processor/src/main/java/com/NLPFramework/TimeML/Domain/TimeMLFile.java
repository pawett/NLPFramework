package com.NLPFramework.TimeML.Domain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Optional;

import org.joda.time.DateTime;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.TimeML.Train.TimeMLTrainTypes;

public class TimeMLFile extends TokenizedFile {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/*
	 * private LinkedList<MakeInstance> makeInstances = new LinkedList<>();
	 * private LinkedList<TimeLink> timeLinks = new LinkedList<>(); private
	 * LinkedList<AspectualLink> aspectualLinks = new LinkedList<>(); private
	 * LinkedList<SubordinationLink> subordinationLinks = new LinkedList<>();
	 */
	private Timex3 DCT = null;

	public TimeMLFile(TokenizedFile file) {
		super(file.getLanguage(), file.getName());
		for (TokenizedSentence sentence : file)
			this.add(sentence);
		this.annotations = file.annotations;
	}

	public TimeMLFile(Language language, String name) {
		super(language, name);
	}

	public void setDCTFromDate(String date) {
		DCT = new Timex3();
		DCT.id = "0";
		DCT.functionInDocument = TimeFunctionInDocument.CREATION_TIME;
		DCT.value = date;
		DCT.temporalFunction = false;
		DCT.type = TimeType.DATE;
	}

	public void setDCT(Timex3 dct) {
		DCT = dct;
	}

	public Timex3 getDCT() {
		if (DCT == null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			String dctFile = sdf.format(new Date());
			setDCTFromDate(dctFile);
		}
		return DCT;
	}

	/*
	 * public void addSubordinationLink(SubordinationLink subordinationLink) {
	 * subordinationLinks.add(subordinationLink); }
	 */

	public LinkedList<SubordinationLink> getSubordinationLinks() {
		LinkedList<SubordinationLink> returnValue = new LinkedList<>();
		if (annotations.get(SubordinationLink.class) == null)
			return returnValue;
		for (Object annotation : annotations.get(SubordinationLink.class)) {
			// for(Object tLink : ((LinkedList<Object>)(annotation)))
			// {
			returnValue.push((SubordinationLink) annotation);
			// }
		}
		return returnValue;
	}

	/*
	 * public void addAspectualLink(AspectualLink aspectualLink) {
	 * aspectualLinks.add(aspectualLink); }
	 */
	public LinkedList<AspectualLink> getAspectualLink() {
		LinkedList<AspectualLink> returnValue = new LinkedList<>();
		if (annotations.get(AspectualLink.class) == null)
			return returnValue;
		for (Object annotation : annotations.get(AspectualLink.class)) {
			// for(Object tLink : ((LinkedList<Object>)(annotation)))
			// {
			returnValue.push((AspectualLink) annotation);
			// }
		}
		return returnValue;
	}
	/*
	 * public void addTimeLink(TimeLink timeLink) { timeLinks.add(timeLink); }
	 */

	public LinkedList<TimeLink> getTimeLinks() {
		LinkedList<TimeLink> returnValue = new LinkedList<>();
		if (annotations.get(TimeLink.class) == null)
			return returnValue;
		for (Object tLinks : annotations.get(TimeLink.class)) {
			/*
			 * for(Object tLink : ((LinkedList<Object>)(tLinks))) {
			 */
			returnValue.push((TimeLink) tLinks);
			// }
		}
		return returnValue;
	}

	public LinkedList<MakeInstance> getMakeInstances() {
		LinkedList<MakeInstance> returnValue = new LinkedList<>();
		if (annotations.get(MakeInstance.class) == null)
			return returnValue;
		for (Object annotation : annotations.get(MakeInstance.class)) {
			/*
			 * for(Object tLink : ((LinkedList<Object>)(annotation))) {
			 */
			returnValue.push((MakeInstance) annotation);
			// }
		}
		return returnValue;
	}

	public MakeInstance getMakeInstanceById(String id) {
		Optional<MakeInstance> mkToReturn = getMakeInstances().stream()
				.filter(mk -> mk != null && mk.id != null && mk.id.equalsIgnoreCase(id)).findFirst();
		if (mkToReturn.isPresent())
			return mkToReturn.get();

		return null;
	}
	
	public MakeInstance getMakeInstanceByVerb(Word verb) {
		Optional<MakeInstance> mkToReturn = getMakeInstances().stream()
				.filter(mk -> mk != null && mk.event != null && mk.event.word.equals(verb)).findFirst();
		if (mkToReturn.isPresent())
			return mkToReturn.get();

		return null;
	}

	public Timex3 getTimexById(String timexID) {
		if (DCT.id.replace("t", "").equalsIgnoreCase(timexID.replace("t", "")))
			return DCT;
		else {
			for (TokenizedSentence sentence : this) {

				if (sentence.annotations.get(Timex3.class) != null) {
					for (Enumeration<Word> e = sentence.annotations.get(Timex3.class).keys(); e.hasMoreElements();) {
						Word w = e.nextElement();
						Timex3 time = (Timex3) sentence.annotations.get(Timex3.class).get(w).element;
						if (time.id.equalsIgnoreCase(timexID))
							return time;
					}
				}
				if (sentence.annotations.get(Timex3Date.class) != null) {
					for (Enumeration<Word> e = sentence.annotations.get(Timex3Date.class).keys(); e
							.hasMoreElements();) {
						Word w = e.nextElement();
						Timex3Date time = (Timex3Date) sentence.annotations.get(Timex3Date.class).get(w).element;
						if (time.id.equalsIgnoreCase(timexID))
							return time;
					}
				}

				if (sentence.annotations.get(Timex3Time.class) != null) {
					for (Enumeration<Word> e = sentence.annotations.get(Timex3Time.class).keys(); e
							.hasMoreElements();) {
						Word w = e.nextElement();
						Timex3Time time = (Timex3Time) sentence.annotations.get(Timex3Time.class).get(w).element;
						if (time.id.equalsIgnoreCase(timexID))
							return time;
					}
				}

				if (sentence.annotations.get(Timex3Duration.class) == null)
					continue;
				for (Enumeration<Word> e = sentence.annotations.get(Timex3Duration.class).keys(); e
						.hasMoreElements();) {
					Word w = e.nextElement();
					Timex3Duration time = (Timex3Duration) sentence.annotations.get(Timex3Duration.class)
							.get(w).element;
					if (time.id.equalsIgnoreCase(timexID))
						return time;
				}

				if (sentence.annotations.get(Timex3Set.class) == null)
					continue;
				for (Enumeration<Word> e = sentence.annotations.get(Timex3Set.class).keys(); e.hasMoreElements();) {
					Word w = e.nextElement();
					Timex3Set time = (Timex3Set) sentence.annotations.get(Timex3Set.class).get(w).element;
					if (time.id.equalsIgnoreCase(timexID))
						return time;
				}
			}
		}

		return null;
	}

	public Event getEventById(String id) {
		for (TokenizedSentence sentence : this) {
			if (sentence.annotations.get(Event.class) == null)
				continue;
			for (Enumeration<Word> e = sentence.annotations.get(Event.class).keys(); e.hasMoreElements();) {
				Word w = e.nextElement();
				Event event = (Event) sentence.annotations.get(Event.class).get(w).element;
				if (event.id.equalsIgnoreCase(id))
					return event;
			}
		}
		return null;
	}

	public Signal getSignalById(String id) {
		for (TokenizedSentence sentence : this) {
			if (sentence.annotations.get(Signal.class) == null)
				continue;
			for (Enumeration<Word> e = sentence.annotations.get(Signal.class).keys(); e.hasMoreElements();) {
				Word w = e.nextElement();
				Signal signal = (Signal) sentence.annotations.get(Signal.class).get(w).element;
				if (signal.id.equalsIgnoreCase(id))
					return signal;
			}
		}
		return null;
	}

	public void cleanAnnotations(TimeMLTrainTypes annotation) {
		for (TokenizedSentence sentence : this) {
			sentence.cleanAnnotations(annotation);
		}
	}

	public int getNumEvents() {
		int totalEvents = 0;
		for (TokenizedSentence sentence : this) {
			int size = 0;
			if (sentence.annotations.get(Event.class) != null)
				size = sentence.annotations.get(Event.class).keySet().size();
			totalEvents += size;
		}
		return totalEvents;
	}

	public int getNumTimex() {
		int totalTimex = 0;
		for (TokenizedSentence sentence : this) {
			int size = 0;
			if (sentence.annotations.get(Timex3.class) != null)
				size = sentence.annotations.get(Timex3.class).keySet().size();
			totalTimex += size;
		}
		return totalTimex;
	}

	private MakeInstance getMakeInstanceFromTimeMLDocument(Word w) {
		Optional<MakeInstance> mkPrevious = getMakeInstances().stream().filter(m -> m.event.word.equals(w)).findFirst();
		if (mkPrevious.isPresent())
			return mkPrevious.get();

		return null;
	}

	private Timex3 getTimexFromTimeMLDocument(Word w) {
		Optional<TimeLink> timex = getTimeLinks().stream()
				.filter(t -> this.getTimexById(t.relatedToTime.id).word.equals(w)).findFirst();
		if (timex.isPresent())
			return this.getTimexById(timex.get().relatedToTime.id);
		return null;
	}
	/*
	 * private void processInterSententialEvents(Word previousSentenceMainEvent,
	 * Word currentSentenceMainEvent) { if(previousSentenceMainEvent != null &&
	 * currentSentenceMainEvent != null) {
	 * 
	 * MakeInstance mkPrevious =
	 * getMakeInstanceFromTimeMLDocument(previousSentenceMainEvent);
	 * 
	 * MakeInstance mkCurrent =
	 * getMakeInstanceFromTimeMLDocument(currentSentenceMainEvent);
	 * if(mkPrevious == null) { mkPrevious = new
	 * MakeInstance(previousSentenceMainEvent, new
	 * Event(previousSentenceMainEvent)); }
	 * 
	 * if(mkCurrent == null) { mkCurrent = new
	 * MakeInstance(currentSentenceMainEvent, new
	 * Event(currentSentenceMainEvent)); }
	 * 
	 * TimeLink tLink = new TimeLink("l" + timeLinks.size() + 1);
	 * tLink.eventInstance = mkPrevious; tLink.relatedToEventInstance =
	 * mkCurrent; timeLinks.add(tLink); timeMLDocument.eEventLinks.add(tLink); }
	 * 
	 * }
	 * 
	 * 
	 * private void processSentenceEventsAndTimes(TokenizedSentence sentence) {
	 * // TODO Auto-generated method stub ArrayList<MakeInstance>
	 * intraSentential = new ArrayList<>(); Timex3 relatedTime = getDCT();
	 * Enumeration<Word> times = sentence.times.keys();
	 * 
	 * while(times.hasMoreElements()) { Word time = times.nextElement();
	 * 
	 * Timex3 timex3 = getTimexFromTimeMLDocument(time);
	 * 
	 * if(timex3 == null) { timex3 = new Timex3(time); timex3.anchorTimeID =
	 * relatedTime; relatedTime = timex3; timeMLDocument.timex3s.add(timex3); }
	 * }
	 * 
	 * Enumeration<Word> events = sentence.events.keys();
	 * 
	 * while(events.hasMoreElements()) { Word event = events.nextElement();
	 * MakeInstance mk = getMakeInstanceFromTimeMLDocument(event); if(mk ==
	 * null) { Event domainEvent = new Event(event); mk = new
	 * MakeInstance(event, domainEvent); timeMLDocument.events.add(domainEvent);
	 * }
	 * 
	 * timeMLDocument.makeInstances.add(mk);
	 * 
	 * if(sentence.times.size() >= 1) { //Event-timex Enumeration<Word> times2 =
	 * sentence.times.keys();
	 * 
	 * while(times2.hasMoreElements()) { Word time = times2.nextElement();
	 * Timex3 timex3 = getTimexFromTimeMLDocument(time);
	 * 
	 * TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
	 * tLink.eventInstance = mk; tLink.relatedToTime = timex3;
	 * timeMLDocument.links.add(tLink); timeMLDocument.eTimexLinks.add(tLink); }
	 * }
	 * 
	 * //sub_event-event if(intraSentential.size() > 0) { for(MakeInstance
	 * prevInstance : intraSentential) { TimeLink tLink = new TimeLink("l" +
	 * timeMLDocument.links.size() + 1); tLink.eventInstance = prevInstance;
	 * tLink.relatedToEventInstance = mk; timeMLDocument.links.add(tLink);
	 * timeMLDocument.subEventEventLinks.add(tLink); } }
	 * 
	 * //Event-DCT
	 * 
	 * TimeLink tLink = new TimeLink("l" + timeMLDocument.links.size() + 1);
	 * tLink.eventInstance = mk; tLink.relatedToTime = timeMLDocument.DCT;
	 * timeMLDocument.links.add(tLink); timeMLDocument.eDctLinks.add(tLink);
	 * 
	 * } }
	 * 
	 * 
	 * public void updateEventTimexFromFileReader(String filePath) {
	 * updateFromFileReader(filePath, timeMLDocument.eTimexLinks, new
	 * TempEvalEventTimeFormatter()); }
	 * 
	 * public void updateEventDCTFromFileReader(String filePath) {
	 * updateFromFileReader(filePath, timeMLDocument.eDctLinks, new
	 * TempEvalEventDCTFormatter()); }
	 * 
	 * public void updateMainEventEventFromFileReader(String filePath) {
	 * updateFromFileReader(filePath, timeMLDocument.eEventLinks, new
	 * TempEvalMainEventEventFormatter()); }
	 * 
	 * public void updateSubEventEventFromFileReader(String filePath) {
	 * updateFromFileReader(filePath, timeMLDocument.subEventEventLinks, new
	 * TempEvalSubEventEventFormatter()); }
	 * 
	 * public void updateFromFileReader(String filePath, List<TimeLink> links,
	 * ITLinkFormatter formatter) { BufferedReader fileReader = null; try {
	 * fileReader = new BufferedReader(new FileReader(filePath));
	 * 
	 * String line="";
	 * 
	 * for(TimeLink link : links) { line = fileReader.readLine(); if (line ==
	 * null )return; if(line.isEmpty()) continue; formatter.setValues(link,
	 * line);
	 * 
	 * }
	 * 
	 * }catch(Exception ex) { Logger.WriteError("Error updating TimeML file",
	 * ex); } finally { if (fileReader != null) { try { fileReader.close(); }
	 * catch (IOException e) { // TODO Auto-generated catch block
	 * Logger.WriteError("Error closing fileReader in TimeML file", e); } }
	 * 
	 * }
	 * 
	 * }
	 * 
	 * public File toFileEventTimex(String path) { return toFile(path,
	 * timeMLDocument.eTimexLinks, new TempEvalEventTimeFormatter()); }
	 * 
	 * public File toFileEventDCT(String path) { return toFile(path,
	 * timeMLDocument.eDctLinks, new TempEvalEventDCTFormatter()); }
	 * 
	 * public File toFileMainEventEvent(String path) { return toFile(path,
	 * timeMLDocument.eEventLinks, new TempEvalMainEventEventFormatter()); }
	 * 
	 * public File toFileSubEventEvent(String path) { return toFile(path,
	 * timeMLDocument.subEventEventLinks, new TempEvalSubEventEventFormatter());
	 * }
	 * 
	 * public File toFile(String path, List<TimeLink> links, ITLinkFormatter
	 * formatter) { String fileName = features.getName() + "." +
	 * formatter.getClass().getName(); if(path != null) fileName = path +
	 * File.separator + fileName;
	 * 
	 * File f = new File(fileName); BufferedWriter output; try { output = new
	 * BufferedWriter(new FileWriter(f.getAbsolutePath()));
	 * 
	 * try{ StringBuilder sb = new StringBuilder(); for(TimeLink link : links) {
	 * sb.append(features.getName()); sb.append(formatter.toString(link));
	 * sb.append(System.lineSeparator()); }
	 * 
	 * output.write(sb.toString()); } catch (IOException e) {
	 * Logger.WriteError("Cannot write in the file" + fileName, e); } finally {
	 * 
	 * if (output != null) { output.close(); } } } catch (IOException e1) {
	 * Logger.WriteError("Cannot create the file in the path" + fileName, e1); }
	 * return f;
	 * 
	 * }
	 * 
	 * 
	 * public String toTML() throws ParserConfigurationException, IOException {
	 * /*DocumentBuilderFactory docFactory =
	 * DocumentBuilderFactory.newInstance(); DocumentBuilder docBuilder =
	 * docFactory.newDocumentBuilder();
	 * 
	 * // root elements org.w3c.dom.Document doc = docBuilder.newDocument(); doc
	 */

	/*
	 * 
	 * File f = new File(features.getName() + ".tml"); BufferedWriter output;
	 * output = new BufferedWriter(new FileWriter(f.getAbsolutePath()));
	 * 
	 * try{
	 * 
	 * StringBuilder sb = new StringBuilder();
	 * sb.append("<?xml version=\"1.0\" ?>"); sb.append(System.lineSeparator());
	 * sb.
	 * append("<TimeML xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://timeml.org/timeMLdocs/TimeML_1.2.1.xsd\">"
	 * ); sb.append(System.lineSeparator()); sb.append("<DOCID>" +
	 * features.getName() + "</DOCID>"); sb.append(System.lineSeparator()); if
	 * (features.getDCT() != null) { sb.append("<DCT><TIMEX3 tid=\"" +
	 * timeMLDocument.DCT.id + "\" type=\"DATE\" value=\"" +
	 * timeMLDocument.DCT.value +
	 * "\" temporalFunction=\"false\" functionInDocument=\"CREATION_TIME\">"
	 * +timeMLDocument.DCT.value + "</TIMEX3></DCT>");
	 * sb.append(System.lineSeparator()); }
	 * 
	 * sb.append("<TEXT>"); sb.append(System.lineSeparator());
	 * 
	 * for(TokenizedSentence sentence : features) { Event mainEvent = null;
	 * Timex3 mainTime = null; String bufferWords = ""; for(Word word :
	 * sentence) {
	 * 
	 * Optional<Event> event = events.stream().filter(e ->
	 * e.word.equals(word)).findFirst(); Optional<Timex3> timex =
	 * timeMLDocument.timex3s.stream().filter(t ->
	 * t.word.equals(word)).findFirst();
	 * 
	 * ArrayList<Word> nextTimexWords = sentence.times.get(word);
	 * 
	 * if(event.isPresent()) { mainEvent = event.get(); ArrayList<Word>
	 * nextEventWords = sentence.events.get(word);
	 * 
	 * if(mainTime != null) { sb.append(printTimex(mainTime, bufferWords));
	 * sb.append(" "); mainTime = null; } bufferWords = word.word;
	 * 
	 * }else if( timex.isPresent()) { mainTime = timex.get();
	 * 
	 * ArrayList<Word> nextTimesWords = sentence.times.get(word);
	 * 
	 * if(mainEvent != null) { sb.append(printEvent(mainEvent, bufferWords));
	 * sb.append(" "); mainEvent = null; }
	 * 
	 * bufferWords = word.word;
	 * 
	 * }else { if(!bufferWords.isEmpty()) { boolean isNextWordEvent = mainEvent
	 * != null && sentence.events.get(mainEvent.word).stream().filter(w ->
	 * w.equals(word)).findFirst().isPresent(); boolean isNextWordTime =
	 * mainTime != null && sentence.times.get(mainTime.word).stream().filter(w
	 * -> w.equals(word)).findFirst().isPresent(); if(isNextWordEvent ||
	 * isNextWordTime) { bufferWords += " "+word; }else { if(mainEvent != null)
	 * sb.append(printEvent(mainEvent, bufferWords)); if(mainTime != null)
	 * sb.append(printTimex(mainTime, bufferWords)); //time or event ended,
	 * print link mainEvent = null; mainTime = null; bufferWords = "";
	 * sb.append(" "); sb.append(word); sb.append(" "); } }else {
	 * sb.append(word); sb.append(" "); } }
	 * 
	 * } sb.append(System.lineSeparator()); }
	 * 
	 * sb.append("</TEXT>");
	 * 
	 * for(MakeInstance mk : makeInstances) { sb.append(printMakeInstances(mk));
	 * sb.append(System.lineSeparator()); }
	 * 
	 * 
	 * for(TimeLink tl : timeLinks) { sb.append(printTimeLink(tl));
	 * sb.append(System.lineSeparator()); }
	 * 
	 * sb.append("</TimeML>");
	 * 
	 * output.write(sb.toString()); } catch (IOException e) {
	 * Logger.WriteError("Cannot write in the file", e); } finally {
	 * 
	 * if (output != null) { output.close(); } }
	 * 
	 * return f.getAbsolutePath();
	 * 
	 * }
	 * 
	 * private String printTimeLink(TimeLink tl) { StringBuilder sb = new
	 * StringBuilder(); sb.append("<TLINK lid=\""+tl.id+"\" relType=\"" +
	 * tl.type.toString() +"\" eventInstanceID=\"" + tl.eventInstance.id +"\"");
	 * if(tl.relatedToTime != null) sb.append("relatedToTime=\"" +
	 * tl.relatedToTime.id +"\" />"); if(tl.relatedToEventInstance != null)
	 * sb.append("relatedToEventInstance=\"" + tl.relatedToEventInstance.id
	 * +"\" />");
	 * 
	 * return sb.toString(); }
	 * 
	 * private String printMakeInstances(MakeInstance mk) { StringBuilder sb =
	 * new StringBuilder(); sb.append("<MAKEINSTANCE eiid=\""+ mk.id +
	 * "\" eventID=\"" + mk.event.id + "\" pos=\"" + mk.pos.toString() +
	 * "\" tense=\"" + mk.tense.toString() +"\" aspect=\"" +
	 * mk.aspect.toString() +"\" polarity=\"" + mk.polarity.toString() +"\"");
	 * 
	 * if(mk.modality != null && !mk.modality.isEmpty()) {
	 * sb.append(" modality=\"" + mk.modality + "\""); }
	 * 
	 * sb.append("/>");
	 * 
	 * return sb.toString(); }
	 * 
	 * private String printTimex(Timex3 timex3, String phrase) { return
	 * "<TIMEX3 tid=\"" + timex3.id + "\" type=\"" + timex3.type.toString() +
	 * "\" value=\"" + timex3.value + "\" temporalFunction=\"" +
	 * timex3.temporalFunction + "\" functionInDocument=\"" +
	 * timex3.functionInDocument + "\" anchorTimeID=\"" + timex3.anchorTimeID.id
	 * +"\">" + phrase + "</TIMEX3>"; }
	 * 
	 * private String printEvent(Event event, String phrase) { return
	 * "<EVENT eid=\"" + event.id + "\" class=\"" + event.eventClass.toString()
	 * + "\">"+ phrase + "</EVENT>"; }
	 */

}
