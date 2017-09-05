package com.NLPFramework.Processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Files.NLPFile;
import com.NLPFramework.Files.PlainFile;
import com.NLPFramework.Files.XMLFile;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.TimeML.Domain.AspectualLink;
import com.NLPFramework.TimeML.Domain.AspectualLinkType;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.EventClass;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.POS;
import com.NLPFramework.TimeML.Domain.Polarity;
import com.NLPFramework.TimeML.Domain.Signal;
import com.NLPFramework.TimeML.Domain.SubordinationLink;
import com.NLPFramework.TimeML.Domain.SubordinationLinkType;
import com.NLPFramework.TimeML.Domain.TimeFunctionInDocument;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeLinkRelationType;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;
import com.NLPFramework.TimeML.Domain.Timex3Date;
import com.NLPFramework.TimeML.Domain.Timex3Duration;
import com.NLPFramework.TimeML.Domain.Timex3Set;
import com.NLPFramework.TimeML.Domain.Timex3Time;
import com.NLPFramework.externalTools.StanfordSynt;

public class TMLExtractor {
	
	
	public static TokenizedFileHashtable getAnnotationsFromDir(File dataPath)
	{
		TokenizedFileHashtable files = new TokenizedFileHashtable();
		List<File> filesFromDir  = FileConverter.getTimeMLFilesFromDir(dataPath, "SemEval4");
		Iterator<Entry<String, TokenizedFile>> it = files.entrySet().iterator();
		TokenizedFileHashtable hashtableFiles = new TokenizedFileHashtable();
		//TODO: extract timeLine
		for(File f : filesFromDir)
		{
			XMLFile xmlfile = null;
			try {
				xmlfile = checkTimeMLFile("SemEval4", Configuration.getLanguage(), dataPath.getAbsolutePath(), f);

				TokenizedFile tokFile = getAnnotationsFromTML(xmlfile.getFile().getCanonicalPath(), Configuration.getLanguage());

				FeaturesFormatter format = new FeaturesFormatter();
				TokenFileFormatter formatter = new TokenFileFormatter(tokFile);

				File dir = new File(f.getCanonicalPath() + "-" + "SemEval4" + "_features/");

				formatter.toFile(dir.getAbsolutePath(), format);
				hashtableFiles.put(f.getName(), tokFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return hashtableFiles;
	}
	
	
	public static TokenizedFile getAnnotationsFromTML(String tmlFilePath, Language lang)
	{
		TimeMLFile tFile = null;
		try
		{
			long startTime = System.currentTimeMillis();
			DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
			Document doc = docBuilder.parse(new File(tmlFilePath));
			
			// normalize text representation
			doc.getDocumentElement().normalize();
		
				
			tFile = processText(tmlFilePath, doc, lang);
			
			//TimeMLDocument tmlDoc = new TimeMLDocument(tFile);
			
			NodeList makeInstances = doc.getElementsByTagName("MAKEINSTANCE");
			processMakeInstances(makeInstances, tFile);
			
			NodeList tLinks = doc.getElementsByTagName("TLINK");
			Logger.WriteDebug("TLinks in file: " + tLinks.getLength());
			processTLinks(tLinks, tFile);
			Logger.WriteDebug("TLinks processed: " + tFile.getTimeLinks().size());
			
			NodeList sLinks = doc.getElementsByTagName("SLINK");
			Logger.WriteDebug("SLinks in file: " + sLinks.getLength());
			processSLinks(sLinks, tFile);
			Logger.WriteDebug("SLinks processed: " + tFile.getSubordinationLinks().size());
			
			NodeList aLinks = doc.getElementsByTagName("ALINK");
			Logger.WriteDebug("ALinks in file: " + aLinks.getLength());
			processALinks(aLinks, tFile);
			Logger.WriteDebug("ALinks processed: " + tFile.getAspectualLink().size());

			long endTime = System.currentTimeMillis();
			long sec=(endTime-startTime)/1000;
			Logger.WriteDebug("Processed in: " + sec +" seconds.");
			
			
		}catch(Exception ex)
		{
			Logger.WriteError("Error extracting tabs from TML", ex);
		}
		return tFile;
	}
	
	private static void processALinks(NodeList aLinks, TimeMLFile tFile)
	{
		for(int i = 0; i < aLinks.getLength(); i++)
		{
			Node makeInstanceNode = aLinks.item(i);
			NamedNodeMap attributes = makeInstanceNode.getAttributes();
			
			String id = getAttributeValue(attributes, "lid");
			MakeInstance eventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "eventInstanceID"));
			MakeInstance relatedToEventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "relatedToEventInstance"));
			AspectualLinkType type = AspectualLinkType.valueOf(getAttributeValue(attributes, "relType"));
			Signal signal = null;
			
			if(getAttributeValue(attributes, "signalID") != null)
				signal = tFile.getSignalById(getAttributeValue(attributes, "signalID"));
			
			AspectualLink aspectualLink = new AspectualLink(id, eventInstance.event, relatedToEventInstance.event, type, signal);
			tFile.addAnnotationWithId(AspectualLink.class, aspectualLink);
			//tFile.addAspectualLink(aspectualLink);					
		}
		
	}

	private static void processSLinks(NodeList sLinks, TimeMLFile tFile) 
	{
		for(int i = 0; i < sLinks.getLength(); i++)
		{
			Node makeInstanceNode = sLinks.item(i);
			NamedNodeMap attributes = makeInstanceNode.getAttributes();
			
			String id = getAttributeValue(attributes, "lid");
			MakeInstance eventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "eventInstanceID"));
			MakeInstance subordinateEventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "subordinatedEventInstance"));
			SubordinationLinkType type = SubordinationLinkType.valueOf(getAttributeValue(attributes, "relType"));
			Signal signal = null;
			
			if(getAttributeValue(attributes, "signalID") != null)
				signal = tFile.getSignalById(getAttributeValue(attributes, "signalID"));
			
			SubordinationLink subordinationLink = new SubordinationLink(id, eventInstance.event, subordinateEventInstance.event, type, signal);
			tFile.addAnnotationWithId(SubordinationLink.class, subordinationLink);
							
		}
		
	}

	private static String getAttributeValue(NamedNodeMap attributes, String attributeName)
	{
		if(attributes.getNamedItem(attributeName) != null)
			return attributes.getNamedItem(attributeName).getNodeValue();
		else
			return null;
	}

	private static void processTLinks(NodeList tLinks, TimeMLFile tFile) 
	{
		
		for(int i = 0; i < tLinks.getLength(); i++)
		{
			Node makeInstanceNode = tLinks.item(i);
			NamedNodeMap attributes = makeInstanceNode.getAttributes();
			String origin = null;
			MakeInstance eventInstance = null;
			MakeInstance relatedToEventInstance = null;
			Timex3 relatedToTime = null;
			TimeLinkRelationType type = null;
			String id = getAttributeValue(attributes, "lid");
			type = TimeLinkRelationType.valueOf(getAttributeValue(attributes, "relType"));
			if( getAttributeValue(attributes, "timeID") != null)
				relatedToTime = tFile.getTimexById(getAttributeValue(attributes, "timeID"));
			
			if(getAttributeValue(attributes, "eventInstanceID") != null)
				eventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "eventInstanceID"));
						
			if(getAttributeValue(attributes, "relatedToEventInstance") != null)
				relatedToEventInstance = tFile.getMakeInstanceById(getAttributeValue(attributes, "relatedToEventInstance"));
			
			if(getAttributeValue(attributes, "relatedToTime") != null)
				relatedToTime = tFile.getTimexById(getAttributeValue(attributes, "relatedToTime"));
			
			origin = getAttributeValue(attributes, "origin");
			
			TimeLink tLink = new TimeLink(origin, eventInstance != null ? eventInstance : null, relatedToEventInstance != null ? relatedToEventInstance : null, relatedToTime != null ? relatedToTime : null, type);
			tLink.id = id;
			tFile.addAnnotationWithId(TimeLink.class,tLink);
		}
	}

	private static void processMakeInstances(NodeList makeInstances, TimeMLFile tFile) 
	{
		for(int i = 0; i < makeInstances.getLength(); i++)
		{
			Node makeInstanceNode = makeInstances.item(i);
			NamedNodeMap attributes = makeInstanceNode.getAttributes();
			
			String id = attributes.getNamedItem("eiid").getNodeValue();
			MakeInstance mk = new MakeInstance(tFile.getEventById(attributes.getNamedItem("eventID").getNodeValue()));
			mk.id = id;
			mk.pos = POS.valueOf(attributes.getNamedItem("pos").getNodeValue());
			mk.polarity = Polarity.valueOf(attributes.getNamedItem("polarity").getNodeValue());
			
			if(attributes.getNamedItem("signalID") != null)
				mk.signal = tFile.getSignalById(attributes.getNamedItem("signalID").getNodeValue());
			
			if(attributes.getNamedItem("cardinality") != null)
				mk.cardinality = attributes.getNamedItem("cardinality").getNodeValue();
			
			if(attributes.getNamedItem("modality") != null)
				mk.modality = attributes.getNamedItem("modality").getNodeValue();
			
			tFile.addAnnotationWithId(MakeInstance.class, mk);
		}
		
	}

	private static TimeMLFile processText(String tmlFilePath, Document doc, Language lang) throws Exception 
	{
	
		
		NodeList texts = doc.getElementsByTagName("TEXT");
		if(texts.getLength() > 1 )
		{
			Logger.WriteError("More than one TEXT tag in the document", null);
			System.exit(1);
		}
			
		
		Element text = (Element) texts.item(0);
		
		
		StringBuilder sb  = new StringBuilder();
		//printChildren(children, sb);
		
		TokenizedFile featuresFile = null;
		String plainFilePath = tmlFilePath + ".plain";
		String fullText = null;
		NodeList sentences = doc.getElementsByTagName("s");
		if(sentences != null && sentences.getLength() > 0)
		{
			featuresFile = extractSentences(tmlFilePath, sb, sentences);
		}else
		{
			fullText = text.getTextContent();
			fullText = FileHelper.formatText(fullText);
			
			//FileHelper.createFileFromText(sb.toString(), plainFilePath);
			FileHelper.createFileFromText(fullText, plainFilePath);
			
			NLPProcessor nlpProcessor = new NLPProcessor(plainFilePath, null);
			/*ArrayList<INLPAction> actions = new ArrayList<>();
			actions.add(new ActionSentenceSplitterCoreNLP(plainFilePath));
			actions.add(new ActionTokenizerCoreNLP());
			TokenizedFile featuresFile = nlpProcessor.execute(actions);*/
			featuresFile = StanfordSynt.run(plainFilePath, Configuration.getLanguage());
			featuresFile.setOriginalText(fullText);
		}
			
		
		
		TimeMLFile tmlFile = new TimeMLFile(featuresFile);
		NodeList timexes = doc.getElementsByTagName("TIMEX3");
		setDCT(tmlFile, timexes);
		
		
		NodeList docIDs = doc.getElementsByTagName("DOCID");
		if(docIDs.getLength() == 1)
		{
			Node docIdInstanceNode = docIDs.item(0);
			String docID = docIdInstanceNode.getFirstChild().getNodeValue();
			tmlFile.setDocId(docID);
		}
		
		ArrayList<Node> flattenedNodes = new ArrayList<>();
		
		NodeList children = text.getChildNodes();
		flattenNodes(children, flattenedNodes);
			
		getEntitiesFromText(tmlFile, flattenedNodes);
		
		return tmlFile;
	}


	private static TokenizedFile extractSentences(String tmlFilePath, StringBuilder sb, NodeList sentences) {
		String fullText;
		TokenizedFile tokFile = new TokenizedFile(Configuration.getLanguage(), tmlFilePath);
		StringBuilder sbDocument = new StringBuilder();
		for(int i = 0; i< sentences.getLength() ; i++)
		{
			Node sentenceNode = sentences.item(i);
			String fullLine = sentenceNode.getTextContent();
			fullLine = FileHelper.formatText(fullLine);
			if(!fullLine.endsWith(".") && !fullLine.endsWith("\""))
			{
				fullLine = fullLine + " .";
				sentenceNode.getLastChild().setNodeValue(sentenceNode.getLastChild().getNodeValue()+ ".");
			}
			sbDocument.append(fullLine+" ");
			sb.append(System.lineSeparator());
			TokenizedSentence sentence = new TokenizedSentence();
			sentence.originalText = fullLine;
			tokFile.add(sentence);
		}
		
		fullText = sbDocument.toString();
		
		tokFile = StanfordSynt.run(tokFile);
		return tokFile;
	}

	private static int getEntitiesFromText(TimeMLFile featuresFile,
			ArrayList<Node> flattenedNodes) 
	{
		int nodePosition = 0;
		String currentNodeWord = null;
		Node current = flattenedNodes.get(nodePosition);
		LinkedList<String> currentNodeWordsList = getNodeValuesAsLinkedList(current);
		
		for(TokenizedSentence sentence : featuresFile)
		{
			
			EntityMapper<Event> eMap = null;
			EntityMapper<Timex3> tMap = null;
			EntityMapper<Signal> sMap = null;
			
			for(Word token : sentence)
			{
				//Logger.Write("FWord: "+token.word + "___________________");
				while(currentNodeWordsList.isEmpty())
				{
					nodePosition++;
					if(nodePosition >= flattenedNodes.size())
						Logger.Write("Error accessing to flattened node in position: " + nodePosition);
					current = flattenedNodes.get(nodePosition);
					//Logger.Write("Position: " + nodePosition + "Current: " + current.getNodeValue());
					currentNodeWordsList = getNodeValuesAsLinkedList(current);
					eMap = null;
					tMap = null;
				}
				//Logger.Write("Current word list: " + currentNodeWordsList);
				currentNodeWord = currentNodeWordsList.pop().trim();	
				
				
				
				if(!areTokensEquals(currentNodeWord, token.word))
				{
					if((token.next != null && areTokensEquals(currentNodeWord, token.next.word))
							|| (sentence.next != null && !sentence.next.isEmpty() && areTokensEquals(sentence.next.getFirst().word, currentNodeWord)))
					{
							currentNodeWordsList.push(currentNodeWord);
							continue;
					}else{
						while(currentNodeWordsList.isEmpty())
						{
							nodePosition++;
							if(nodePosition >= flattenedNodes.size())
								Logger.Write("Error accessing to flattened node in position: " + nodePosition);
							current = flattenedNodes.get(nodePosition);
						//	Logger.Write("Position: " + nodePosition + "Current: " + current.getNodeValue());
							currentNodeWordsList = getNodeValuesAsLinkedList(current);
							eMap = null;
							tMap = null;
						}
					//	Logger.Write("Current word list: " + currentNodeWordsList);
						currentNodeWord = currentNodeWordsList.pop().trim();
					}
				}
				
				/*while(!areTokensEquals(currentNodeWord, token.word))
				{
					while(currentNodeWordsList.isEmpty())
					{
						nodePosition++;
						if(nodePosition >= flattenedNodes.size())
							Logger.Write("Error accessing to flattened node in position: " + nodePosition);
						current = flattenedNodes.get(nodePosition);
						Logger.Write("Position: " + nodePosition + "Current: " + current.getNodeValue());
						currentNodeWordsList = getNodeValuesAsLinkedList(current);
						eMap = null;
						tMap = null;
					}
					Logger.Write("Current word list: " + currentNodeWordsList);
					currentNodeWord = currentNodeWordsList.pop().trim();
				}*/
				
				//Logger.Write("FWord: "+token.word + "  TMLWord: "+ currentNodeWord);
				
				boolean isEvent = current.getParentNode().getNodeName().equalsIgnoreCase("event");
				if(isEvent)
				{	
					eMap = getEventFromText(sentence, current, eMap, token);
					continue;
				}
				
				boolean isTime = current.getParentNode().getNodeName().equalsIgnoreCase("timex3");
				if(isTime)
				{
					tMap = getTimex3FromText(featuresFile, sentence, current, tMap, token);
					continue;
				}
				
				boolean isSignal = current.getParentNode().getNodeName().equalsIgnoreCase("signal");
				if(isSignal)
				{
					getSignalFromText(sentence, current, sMap, token);
					continue;
				}
				
				
			}
		}
		return nodePosition;
	}

	private static boolean areTokensEquals(String currentNodeWord, String word) {
		// TODO Auto-generated method stub
		boolean tokensAreQuotes = word.matches("''|``|\"") && currentNodeWord.matches("''|``|\"");
		return currentNodeWord.equalsIgnoreCase(word) || tokensAreQuotes;
	}

	private static void getSignalFromText(TokenizedSentence sentence, Node current, EntityMapper<Signal> sMap,
			Word token) 
	{
		if(sMap == null)
		{
			Node parentNode = current.getParentNode();
			NamedNodeMap parentNodeAttributes = parentNode.getAttributes();
			Signal signal = new Signal();
			sMap = new EntityMapper<>();
			sMap.element = signal;
			sMap.firstWordPosition = token.sentencePosition;
			signal.id = parentNodeAttributes.getNamedItem("sid").getNodeValue();
			sentence.addAnnotationWithId(Signal.class, token, sMap);
		}else
			sMap.endWordPosition = token.sentencePosition;
	}

	private static EntityMapper<Timex3> getTimex3FromText(TimeMLFile file, TokenizedSentence sentence, Node current, EntityMapper<Timex3> tMap,
			Word token)
	{
		if(tMap == null)
		{
			
			Node parentNode = current.getParentNode();
			NamedNodeMap parentNodeAttributes = parentNode.getAttributes();
			
			String type = parentNodeAttributes.getNamedItem("type").getNodeValue();
			String value = parentNodeAttributes.getNamedItem("value").getNodeValue();
			Timex3 time;
			switch(type.toLowerCase())
			{
				case "date":
					time = new Timex3Date(token);
					break;
				case "duration":
					time = new Timex3Duration(token);
					if(parentNodeAttributes.getNamedItem("beginPoint") != null)
					{
						String anchorTimeID = parentNodeAttributes.getNamedItem("beginPoint").getNodeValue();
						((Timex3Duration)time).beginPoint = file.getTimexById(anchorTimeID);
					}
					if(parentNodeAttributes.getNamedItem("endPoint") != null)
					{
						String anchorTimeID = parentNodeAttributes.getNamedItem("endPoint").getNodeValue();
						((Timex3Duration)time).endPoint = file.getTimexById(anchorTimeID);
					}
					break;
				case "set": 
					time = new Timex3Set(token);
					if(parentNodeAttributes.getNamedItem("freq") != null)
						((Timex3Set)time).freq = parentNodeAttributes.getNamedItem("freq").getNodeValue();
					if(parentNodeAttributes.getNamedItem("quant") != null)
						((Timex3Set)time).quant = parentNodeAttributes.getNamedItem("quant").getNodeValue();
					break;
				case "time":
					time = new Timex3Time(token);
					break;
				default:
					time = new Timex3(token);
			}
			
			time.value = value;
			time.id = parentNodeAttributes.getNamedItem("tid").getNodeValue();
			if(parentNodeAttributes.getNamedItem("anchorTimeID") != null)
			{
				String anchorTimeID = parentNodeAttributes.getNamedItem("anchorTimeID").getNodeValue();
				time.anchorTimeID = anchorTimeID;
			}
			
			if(parentNodeAttributes.getNamedItem("functionInDocument") != null)
				time.functionInDocument = TimeFunctionInDocument.valueOf(parentNodeAttributes.getNamedItem("functionInDocument").getNodeValue());
			
			if(parentNodeAttributes.getNamedItem("temporalFunction") != null)
				time.temporalFunction = Boolean.parseBoolean(parentNodeAttributes.getNamedItem("temporalFunction").getNodeValue());
			
			tMap = new EntityMapper<>();
			
			tMap.element = time;
			tMap.firstWordPosition = token.sentencePosition;
			tMap.endWordPosition = token.sentencePosition;
			
			sentence.addAnnotationWithId(time.getClass(), token, tMap);
																
			/*token.id = namedNode.getNamedItem("tid").getNodeValue();
			token.norm_type2 = namedNode.getNamedItem("type").getNodeValue();
			token.norm_type2_value = namedNode.getNamedItem("value").getNodeValue();
			sentence.times.put(token, new ArrayList<>());*/
		}
		else
			tMap.endWordPosition = token.sentencePosition;
		
		return tMap;
	}

	private static EntityMapper<Event> getEventFromText(TokenizedSentence sentence, Node current, EntityMapper<Event> eMap,
			Word token) {
		if(eMap == null)
		{		
			token.isVerb = token.pos.startsWith("VB");
			Node parentNode = current.getParentNode();
			NamedNodeMap parentNodeAttributes = parentNode.getAttributes();
			
			Event event = new Event(token);
			event.id = parentNodeAttributes.getNamedItem("eid").getNodeValue();
			if(parentNodeAttributes.getNamedItem("class") != null)
				event.eventClass = EventClass.valueOf(parentNodeAttributes.getNamedItem("class").getNodeValue());
			
			eMap = new EntityMapper<>();
			
			eMap.element = event;
			eMap.firstWordPosition = token.sentencePosition;
			eMap.endWordPosition = token.sentencePosition;
			
			sentence.addAnnotationWithId(Event.class, token, eMap);
			
			/*token.element_type_class = namedNode.getNamedItem("class").getNodeValue();
			token.id = namedNode.getNamedItem("eid").getNodeName();
			sentence.events.put(token, new ArrayList<>());*/
		}
		else
			eMap.endWordPosition = token.sentencePosition;
		
		return eMap;
	}

	private static void setDCT(TimeMLFile featuresFile, NodeList timexes) {
		if(timexes.getLength() > 0)
		{
			Node dctNode = timexes.item(0);
			if(dctNode.getAttributes().getNamedItem("tid").getNodeValue().equalsIgnoreCase("t0"))
			{
				String dctValue = dctNode.getAttributes().getNamedItem("value").getNodeValue();
				Timex3 timeDCT = new Timex3Date(null);
				timeDCT.id = dctNode.getAttributes().getNamedItem("tid").getNodeValue().replaceAll("t", "");
				timeDCT.value = dctNode.getAttributes().getNamedItem("value").getNodeValue();
				timeDCT.functionInDocument = TimeFunctionInDocument.valueOf(dctNode.getAttributes().getNamedItem("functionInDocument").getNodeValue());
				if(dctNode.getAttributes().getNamedItem("temporalFunction") != null)
					timeDCT.temporalFunction = Boolean.parseBoolean(dctNode.getAttributes().getNamedItem("temporalFunction").getNodeValue());
				featuresFile.setDCT(timeDCT);
			}
		}else
		{
			Logger.WriteDebug("No DCT found in this file");
		}
	}
	
	private static LinkedList<String> getNodeValuesAsLinkedList(Node node)
	{
		return StanfordSynt.tokenize(FileHelper.formatText(node.getNodeValue()));
		/*LinkedList<String> currentNodeWordsList = new LinkedList<>();
		String[] currentNodeWords = node.getNodeValue().split("\\s+");
		
		for(int i = 0; i < currentNodeWords.length; i++)
		{
			currentNodeWordsList.add(currentNodeWords[i]);
		}
		return currentNodeWordsList;*/
	}
	
	private static void flattenNodes(NodeList nodes, ArrayList<Node> flattenedNodes)
	{
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node current = nodes.item(i);
			if(current.hasChildNodes())
			{
				
				flattenNodes(current.getChildNodes(), flattenedNodes);
			}else
			{
				if(!current.getNodeValue().matches("\\s"))
					flattenedNodes.add(current);
			}
		}
	}
	
	private static void getFeaturesFromTML(Word w, NodeList nodes)
	{
		for(int i = 0; i < nodes.getLength(); i++)
		{
			Node current = nodes.item(i);
			if(current.hasChildNodes())
			{
				
			}else
			{
				String nodeContent = current.getTextContent();
				while(nodeContent.lastIndexOf(w.word) == (nodeContent.length()-1))
				{
					w = w.next;
				}	
			}
		}
	}
	
	public static void printChildren(NodeList children, StringBuilder sb)
	{
		for(int i = 0; i < children.getLength(); i++)
		{
			if(children.item(i).hasChildNodes())
				printChildren(children.item(i).getChildNodes(), sb);
			else 
				sb.append(children.item(i).getTextContent());
		}

	}
	
	private static XMLFile checkTimeMLFile(String approach, Language lang, String featuresdir, File tmlfile)
			throws Exception, IOException 
	{
		if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
		    System.err.println("File: " + tmlfile.getAbsolutePath());
		}
		NLPFile nlpfile = new PlainFile(tmlfile.getAbsolutePath());
		nlpfile.setLanguage(lang);
		if (!(FileUtils.getNLPFormat(nlpfile.getFile())).equalsIgnoreCase("XML")) {
		   // throw new Exception("TimeML (.tml) XML file is required as input. Found: " + nlpfile.getFile().getCanonicalPath());
			return  null;
		}

		XMLFile xmlfile = new XMLFile(nlpfile.getFile().getAbsolutePath(),null);
		xmlfile.setLanguage(lang);
		if (!xmlfile.getExtension().equalsIgnoreCase("tml")) {
		    throw new Exception("TimeML (.tml) XML file is required as input.");
		}

		if (!xmlfile.isWellFormatted()) {
		    throw new Exception("File: " + xmlfile.getFile() + " is not a valid TimeML (.tml) XML file.");
		}

		// Create a working directory (commented because that way we can reuse roth-freeling annotations)
		File dir = new File(nlpfile.getFile().getCanonicalPath() + "-" + approach + "_features/");
		if (!dir.exists() || !dir.isDirectory()) {
		    dir.mkdir();
		}
		// Copy the valid TML-XML file
		String output = dir + File.separator + nlpfile.getFile().getName();
		FileUtils.copyFileUtil(nlpfile.getFile(), new File(output));
		xmlfile = new XMLFile(output,null);

		return xmlfile;

	}

}
