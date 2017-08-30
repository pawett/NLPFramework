package com.NLPFramework.Processor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.EventTime;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Files.NLPFile;
import com.NLPFramework.Files.PlainFile;
import com.NLPFramework.Files.XMLFile;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Formatters.Types.IllinoisCoref.FileReader;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.TimeML.Domain.TimeLine;
import com.NLPFramework.TimeML.Domain.TimeMLFile;

public class GetData implements IActionExecutor {

	private File dataPath = null;
	public GetData(String path)
	{
		dataPath = new File(path);
	}
	
	public GetData(File path)
	{
		dataPath = path;
	}
	
	@Override
	public void execute() throws Exception 
	{
		String featuresdir = dataPath.getParent() + File.separator + dataPath.getName() + "_" + "SemEval4" + "_features";
		String featuresTrainFilePath = featuresdir + File.separator + "features.obj";
		TokenizedFileHashtable files = new TokenizedFileHashtable();
		// Check for features files (train/test)
		if (!new File(featuresTrainFilePath).exists()) 
		{
			//files = TMLExtractor.getAnnotationsFromDir(dataPath);
			List<File> filesFromDir  = FileConverter.getTimeMLFilesFromDir(dataPath, "SemEval4");
			/*Iterator<Entry<String, TokenizedFile>> it = files.entrySet().iterator();
			TokenizedFileHashtable hashtableFiles = new TokenizedFileHashtable();
			//TODO: extract timeLine
			for(File f : filesFromDir)
			{
				XMLFile xmlfile = checkTimeMLFile("SemEval4", Configuration.getLanguage(), dataPath.getAbsolutePath(), f);
				TokenizedFile tokFile = TMLExtractor.getAnnotationsFromTML(xmlfile.getFile().getCanonicalPath(), Configuration.getLanguage());
				FeaturesFormatter format = new FeaturesFormatter();
				TokenFileFormatter formatter = new TokenFileFormatter(tokFile);
				
				File dir = new File(f.getCanonicalPath() + "-" + "SemEval4" + "_features/");
				formatter.toFile(dir.getAbsolutePath(), format);
				hashtableFiles.put(f.getName(), tokFile);
			}*/
			
			for(File f : filesFromDir)
			{
				TokenizedFile file = TMLExtractor.getAnnotationsFromTML(f.getAbsolutePath(), Configuration.getLanguage());
				
				NLPProcessor processor = new NLPProcessor(file);
				
				processor.classifyEvents();
				processor.setMakeInstancesFromEvents();
				processor.processTimex();
				processor.setIds();
				processor.RecognizeTLINKS();
				processor.executeCoreference();
				
				ActionNERCoreNLP coreNLPNer = new ActionNERCoreNLP();
				coreNLPNer.execute(file);
				
				ActionNEDDbPedia ned = new ActionNEDDbPedia();
				ned.execute(file);
				
				files.put(f.getName(), file);
				
			}
			/*Iterator<Map.Entry<String,TokenizedFile>> it = files.entrySet().iterator();
			while(it.hasNext())
			{
				TimeMLFile file = (TimeMLFile) it.next().getValue();
				
				NLPProcessor processor = new NLPProcessor(file);
				
				processor.classifyEvents();
				processor.setMakeInstancesFromEvents();
				processor.processTimex();
				processor.setIds();
				processor.RecognizeTLINKS();
				processor.executeCoreference();
				
				ActionNERCoreNLP coreNLPNer = new ActionNERCoreNLP();
				coreNLPNer.execute(file);
				
				ActionNEDDbPedia ned = new ActionNEDDbPedia();
				ned.execute(file);	
				
			}*/
			
			FileHelper.saveFilesAsBinary(files, featuresdir);
			
		}else
		{
			files = FileHelper.getBinaryFiles(featuresTrainFilePath);
		}
		
		
		Iterator<Map.Entry<String,TokenizedFile>> it = files.entrySet().iterator();
		TimeLine tl = new TimeLine();
		while(it.hasNext())
		{
			TokenizedFile file = it.next().getValue();
			TimeMLFile timeMlFile = (TimeMLFile)file;
			String jsonPath = dataPath + File.separator + file.getName() + ".json";
			FileReader fr = new FileReader();
			//IllinoisCoreference coref =  fr.readFile(jsonPath);
			//TODO:Move NER to processor
			
			
			/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			String dctFile = sdf.format(new Date());
			Timex3 dctTimex = ((TimeMLFile)file).getDCT();
			Date dct = dctTimex != null ? DateTime.parse(dctTimex.value).toDate() : DateTime.parse(dctFile).toDate();
			TokenFileFormatter formatter = new TokenFileFormatter(file);
			formatter.updateFromExternalTool(new StanfordNER(dct));
			*/
			ActionProcessNER processNER = new ActionProcessNER();
			processNER.execute(timeMlFile);
			
			TimeLineProcessor tlProcessor = new TimeLineProcessor(timeMlFile);
			tlProcessor.execute(tl);
		}
		
		HashMap<String, EventTime> entityMap = tl.getEntityMap();
		ArrayList<String> eventsOrdered = new ArrayList<>(entityMap.keySet());
		Collections.sort(eventsOrdered);
		
		for(String entity : eventsOrdered)
		{
			Logger.Write("Events for " + entity.toString() + " num: "+ entityMap.get(entity).getTotalEvents());
			Logger.Write("EventTimes: " + entityMap.get(entity).getEventTimesOrderedByTime());
			
		}
		
		/*ArrayList<NER> entities = tl.getEntities();
		ArrayList<EventWithContext> events = tl.getEvents();
		UndirectedGraph<String, DefaultEdge> graph =  tl.getGraph();
		
		for(EventWithContext entity : events)
		{
			Logger.Write("Events for " + entity.toString());
			Set<DefaultEdge> edges = graph.edgesOf(entity.toString());
			for(DefaultEdge edge : edges)
			{
				
				Logger.Write("Time: " + graph.getEdgeSource(edge));
				Logger.Write("Entity: " + graph.getEdgeTarget(edge));
				int i = 0;
			}
		}*/
		//createTimeLine
		

	}
	
	

}
