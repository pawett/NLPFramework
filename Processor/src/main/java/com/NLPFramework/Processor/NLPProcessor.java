package com.NLPFramework.Processor;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ITokenizer;
import com.NLPFramework.Formatters.SennaFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Formatters.TimeML.EventClassikAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventClassikFormatter;
import com.NLPFramework.Formatters.TimeML.EventDCTRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventDCTRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventEventRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventSubEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventSubEventRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationFormatter;
import com.NLPFramework.Formatters.TimeML.FeaturesEventAnnotatedFormatter;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;
import com.NLPFramework.TimeML.Domain.Timex3Date;
import com.NLPFramework.TimeML.Domain.Timex3Duration;
import com.NLPFramework.TimeML.Domain.Timex3Set;
import com.NLPFramework.TimeML.Domain.Timex3Time;
import com.NLPFramework.externalTools.FreeLing;
import com.NLPFramework.externalTools.ITextProcessor;
import com.NLPFramework.externalTools.SVM;
import com.NLPFramework.externalTools.Senna;
import com.NLPFramework.externalTools.StanfordNER;
import com.NLPFramework.externalTools.StanfordSynt;


public class NLPProcessor 
{
	//private String approach;
	private TokenizedFile file;
	private File originalFile = null;
	private TemporalInformationProcessingStrategy method = new TemporalInformationProcessingStrategy(Configuration.getLanguage());
	
	TimeML tml = null;
	
	public NLPProcessor(TokenizedFile file)
	{
		this.file = file;
	}
	
	public NLPProcessor(String originalFilePath, TemporalInformationProcessingStrategy method)
	{
		originalFile = new File(originalFilePath);
		String fileName = originalFile.getName();
		
		this.method = method;
		file = new TokenizedFile(Configuration.getLanguage(), fileName);
	}
	
	public TokenizedFile execute(ArrayList<INLPActionFile> actions)
	{
		
		ArrayList<Class<? extends INLPActionFile>> dependencies = new ArrayList<>();
		for(INLPActionFile action : actions)
		{
			if(action.getDependencies() == null)
				continue;
			for(Class<? extends INLPActionFile> depaction : action.getDependencies())
			{
				if(!dependencies.contains(depaction))
					dependencies.add(depaction);
			}
			
		}
		
		for(Class<? extends INLPActionFile> actionToExecute : dependencies)
		{
			try {
				file = Configuration.getClassForAction(actionToExecute, originalFile.getAbsolutePath()).execute(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				Logger.WriteError("Error executing an action", e);
			}
		}
		
		for(INLPActionFile action : actions)
		{
			try {
				action.execute(file);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		
		return file;
	}
	
	public TokenizedFile getFile()
	{
		return file;
	}
	
	public TimeML getTimeMLFile()
	{
		return tml;
	}
	
	public void setIds()
	{
		Logger.WriteDebug("Setting IDs");
		int currentEventCount = 1;
		int currentTimexCount = 1;
		for(TokenizedSentence sentence : file)
		{
			Hashtable<Word,EntityMapper<Annotation>> events =  sentence.annotations.get(Event.class);
			if(events != null)
			{
				for(Word w : events.keySet())
				{
					EntityMapper<Annotation> e = events.get(w);
					e.element.id = "" +currentEventCount++;
				}
			}
		
			Hashtable<Word,EntityMapper<Annotation>> timex =  sentence.annotations.get(Timex3.class);
			if(timex != null)
			{
				for(Word w : timex.keySet())
				{
					EntityMapper<Annotation> t = timex.get(w);
					t.element.id = "" + currentTimexCount++;
				}
			}
			
			timex =  sentence.annotations.get(Timex3Date.class);
			if(timex != null)
			{
				for(Word w : timex.keySet())
				{
					EntityMapper<Annotation> t = timex.get(w);
					t.element.id = "" + currentTimexCount++;
				}
			}
			
			timex =  sentence.annotations.get(Timex3Duration.class);
			if(timex != null)
			{
				for(Word w : timex.keySet())
				{
					EntityMapper<Annotation> t = timex.get(w);
					t.element.id = "" + currentTimexCount++;
				}
			}
			
			timex =  sentence.annotations.get(Timex3Set.class);
			if(timex != null)
			{
				for(Word w : timex.keySet())
				{
					EntityMapper<Annotation> t = timex.get(w);
					t.element.id = "" + currentTimexCount++;
				}
			}
			
			timex =  sentence.annotations.get(Timex3Time.class);
			if(timex != null)
			{
				for(Word w : timex.keySet())
				{
					EntityMapper<Annotation> t = timex.get(w);
					t.element.id = "" + currentTimexCount++;
				}
			}
		}
		
	}
	
	public TokenizedFile tokenizeFile(ITokenizer tokenizer)
	{
		tokenizer.run(originalFile.getAbsolutePath(), file);
		return file;
	}
	
	public TokenizedFile setSemanticFeatures()
	{
		ActionSemanticParserSenna sennaSRL = new ActionSemanticParserSenna();
		sennaSRL.execute(file);
		
		return file;
	}
	
	
	
	
	
	public TokenizedFile setFeatures() throws Exception
	{		
		IFeatureExtractorStrategy featureExtractor = null;
		
		switch(Configuration.getLanguage())
		{
		case EN:
			//file = StanfordSynt.run(originalFile.getAbsolutePath(), Configuration.getLanguage());
			//file = setSemanticFeatures();
			//ActionCoreferenceCoreNLP sem = new ActionCoreferenceCoreNLP();
			//sem.execute(file);
			featureExtractor = new FeatureExtractorEnglish();
			break;
		case ES:
			file = tokenizeFile(new FreeLing());
		    featureExtractor = new FeatureExtractorSpanish();
			break;
		default:
			throw new Exception();
		}
			
	
		file = featureExtractor.setFeatures(file);
		//output = GetPairSpecialTemEval2FeaturesFromString(lang, file, output);
		return file;
	}
	

	public void processTimex()
	{
		Logger.WriteDebug("Recognizing TIMEX3s");
		
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			String dctFile = sdf.format(new Date());
			Timex3 dctTimex = ((TimeMLFile)file).getDCT();
			Date dct = dctTimex != null ? DateTime.parse(dctTimex.value).toDate() : DateTime.parse(dctFile).toDate();
			TokenFileFormatter formatter = new TokenFileFormatter(file);
			formatter.updateFromExternalTool(new StanfordNER(dct));
		}catch(Exception ex)
		{
			Logger.WriteError("Error Recognizing timex3", ex);
		}
	/*	method.getTimexProcessing().getRecognition().Test(file, modelsPath, approach,"rec_timex", language.toString(), new TempEval2FeaturesFormatter(), new TempEval2FeaturesAnnotated());
		
		file.setType(FilesType.TempEval2_features_annotatedWith);
		Logger.WriteDebug("Recognizing TIMEX3s: "+file);
		
		processFileTimes();
		
		Logger.WriteDebug("Classifying TIMEX3s");

		Classification.setClassikTimex(file);
		file.setType(FilesType.TempEval_classik_features);
		
		method.getTimexProcessing().getClassification().Test(file, modelsPath, approach,"class_timex", language.toString(),new TempEvalClassikFormatter(), new TempEvalClassikAnnotatedFormatter());
		String timex_class = file.toString();

		

		TimexNormalization.getTIMEN_new(file);
		file.setType(FilesType.TempEval_normalization);
		method.getTimexProcessing().getNormalization().Test(file, modelsPath, approach, "timen_timex",  language.toString(), new TempEvalClassikFeaturesAnnotatedNormalizationFormatter(), new TempEvalClassikFeaturesAnnotatedNormalizationFormatter());
		

		TimexNormalization.normalizeValues(file);
		*/
		//TimexNormalization.normalize(file);
		
		//formatter = new TokenFileFormatter(file);
		//formatter.toFile(null, new TempEvalClassikFeaturesAnnotatedNormalizationNormalizedFormatter());
		
	}
	
	/*private void processFileTimes() 
	{
	
		for(TokenizedSentence sentence : file)
		{
			Word currentSentenceTime = null;
			for(Word token :	sentence) 
			{
				if(token.time_IOB2.matches("B-(?:timex|TIMEX)3?.*"))
				{
					sentence.times.put(token, new ArrayList<Word>());
					sentence.times.get(token).add(token);
					currentSentenceTime = token;
				}else if(token.time_IOB2.matches("I-(?:timex|TIMEX)3?.*"))
				{
					if(currentSentenceTime == null)
					{
						Logger.WriteDebug("There is not a currentTime for this token: "+ token + " sentence:" + sentence);
						sentence.times.put(token, new ArrayList<Word>());
						sentence.times.get(token).add(token);
					}
					else
					{
						Logger.WriteDebug(sentence + " : " + token + " Time: "+ currentSentenceTime);

						sentence.times.get(currentSentenceTime).add(token);
					}
				}

			}
		}
		// TODO Auto-generated method stub
		
	}*/

	public void recogniceEvents()
	{
		Logger.WriteDebug("Recognizing EVENTs");
			
		method.getEventProcessing().getRecognition().Test(file, Configuration.getModelsPath(), Configuration.getApproach(), "recognition_event_VB", file.getLanguage(), new FeaturesFormatter(), new FeaturesEventAnnotatedFormatter());
		method.getEventProcessing().getRecognition().Test(file, Configuration.getModelsPath(), Configuration.getApproach(), "recognition_event_NN", file.getLanguage(), new FeaturesFormatter(), new FeaturesEventAnnotatedFormatter());
		method.getEventProcessing().getRecognition().Test(file, Configuration.getModelsPath(), Configuration.getApproach(), "recognition_event_JJ", file.getLanguage(), new FeaturesFormatter(), new FeaturesEventAnnotatedFormatter());
		
		//method.getEventProcessing().getRecognition().Test(file, modelsPath, approach, "rec_event", language.toString(), new TempEval2FeaturesFormatter(), new TempEval2FeaturesAnnotated());
		
		//Classification.setClassikEvents(file);
		
		//method.getEventProcessing().getClassification().Test(file, Configuration.getModelsPath(), approach, "classification_event", Configuration.getLanguage(), new TempEvalClassikEventFormatter(), new TempEvalClassikAnnotatedFormatter());
		
	}
	
	public void classifyEvents()
	{

		Logger.WriteDebug("Classifying EVENTs");

		method.getEventProcessing().getClassification().Test(file, Configuration.getModelsPath(), Configuration.getApproach(), "classification_event", file.getLanguage(), new EventClassikFormatter(), new EventClassikAnnotatedFormatter());
		
	}
	
	public void executeCoreference()
	{
		ActionCoreferenceCoreNLP action = new ActionCoreferenceCoreNLP();
		try {
			action.execute(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setMakeInstancesFromEvents()
	{
		Logger.WriteDebug("Setting MAKEINSTANCES");
		TimeMLFile timeFeatures = (TimeMLFile)file;
		tml = new TimeML(timeFeatures);

		for(TokenizedSentence sentence : timeFeatures)
		{	
			//Process makeinstances
			if(sentence.annotations.get(Event.class) != null)
			{
				for(Word key : sentence.annotations.get(Event.class).keySet())
				{
					MakeInstance mkCurrent = new MakeInstance(key, (Event)sentence.annotations.get(Event.class).get(key).element);
					//mkCurrent.id = "mk" + (timeFeatures.annotations.get(MakeInstance.class).size() + 1);
					timeFeatures.addAnnotation(MakeInstance.class, mkCurrent);
				}
			}
		}
	}
	
	public void RecognizeTLINKS()
	{
		Logger.WriteDebug("Recognizing TLINKs");

		TimeMLFile timeFeatures = (TimeMLFile)file;
		tml = new TimeML(timeFeatures);
		
		MakeInstance previousSentenceMainEvent = null;
		MakeInstance currentSentenceMainEvent = null;
		
		if(timeFeatures.getAnnotations(MakeInstance.class) == null)
			setMakeInstancesFromEvents();
		
		for(TokenizedSentence sentence : timeFeatures)
		{	

		
			//Process makeinstances
			/*if(sentence.annotations.get(Event.class) != null)
			{
				for(Word key : sentence.annotations.get(Event.class).keySet())
				{
					MakeInstance mkCurrent = new MakeInstance(key, (Event)sentence.annotations.get(Event.class).get(key).element);
					//mkCurrent.id = "mk" + (timeFeatures.annotations.get(MakeInstance.class).size() + 1);
					timeFeatures.addAnnotation(MakeInstance.class, mkCurrent);
				}
			}*/
			
			currentSentenceMainEvent = TimeMLHelper.getMakeInstanceFromFile(timeFeatures, TimeMLHelper.getSentenceMainEvent(sentence));
			
			if(currentSentenceMainEvent != null)
			{
				//Event-DCT

				TimeLink tLink = new TimeLink();
				tLink.eventInstance = currentSentenceMainEvent;
				tLink.relatedToTime = timeFeatures.getDCT();
				timeFeatures.addAnnotation(TimeLink.class, tLink);
				
			}
			
			//Process main events
			TimeLink tLink = new TimeLink();
			if(previousSentenceMainEvent != null && currentSentenceMainEvent != null)
			{
			
				tLink.eventInstance = previousSentenceMainEvent;
				tLink.relatedToEventInstance = currentSentenceMainEvent;
				timeFeatures.addAnnotation(TimeLink.class, tLink);
			}

			previousSentenceMainEvent = currentSentenceMainEvent;// != null ? currentSentenceMainEvent;
			processSentenceEventsAndTimes(timeFeatures, sentence);
		}


		new SVM().Test(timeFeatures, Configuration.getModelsPath(), Configuration.getApproach(), "classification_e-dct", file.getLanguage(), new EventDCTRelationFormatter(), new EventDCTRelationAnnotatedFormatter());

		new SVM().Test(timeFeatures, Configuration.getModelsPath(), Configuration.getApproach(), "classification_e-t", file.getLanguage(), new EventTimexRelationFormatter(), new EventTimexRelationAnnotatedFormatter());

		new SVM().Test(timeFeatures, Configuration.getModelsPath(), Configuration.getApproach(), "classification_e-e", file.getLanguage(), new EventEventRelationFormatter(), new EventEventRelationAnnotatedFormatter());

		new SVM().Test(timeFeatures, Configuration.getModelsPath(), Configuration.getApproach(), "classification_e-sube", file.getLanguage(), new EventSubEventRelationFormatter(), new EventSubEventRelationAnnotatedFormatter());

	}
	

	private void processSentenceEventsAndTimes(TimeMLFile file, TokenizedSentence sentence) {
		// TODO Auto-generated method stub
		ArrayList<MakeInstance> intraSentential = new ArrayList<>();
		Timex3 relatedTime = file.getDCT();
		

	/*	while(times.hasMoreElements())
		{
			Word time = times.nextElement();
			
			Timex3 timex3 = TimeMLHelper.getTimexFromFile(file, time);

			if(timex3 == null)
			{
				timex3 = new Timex3(time);
				timex3.anchorTimeID = relatedTime.id;
				relatedTime = timex3;
				timeMLDocument.timex3s.add(timex3);
				
			}	
		}*/

		Enumeration<Word> events = sentence.annotations.get(Event.class) != null ? sentence.annotations.get(Event.class).keys() : null;

		while(events != null && events.hasMoreElements())
		{
			Word event = events.nextElement();
			MakeInstance mk = TimeMLHelper.getMakeInstanceFromFile(file, event);
			
			if(sentence.annotations.get(Timex3.class) != null && sentence.annotations.get(Timex3.class).keySet().size() >= 1)
			{
				//Event-timex
				Enumeration<Word> times2 = sentence.annotations.get(Timex3.class).keys();

				while(times2.hasMoreElements())
				{
					Word time = times2.nextElement();
					Timex3 timex3 =  TimeMLHelper.getTimexFromFile(file, time);

					TimeLink tLink = new TimeLink();
					tLink.eventInstance = mk;
					tLink.relatedToTime = timex3;
					file.addAnnotation(TimeLink.class, tLink);
				}
			}

			//sub_event-event
			if(intraSentential.size() > 0)
			{
				for(MakeInstance prevInstance : intraSentential)
				{
					TimeLink tLink = new TimeLink();
					tLink.eventInstance = prevInstance;
					tLink.relatedToEventInstance = mk;
					file.addAnnotation(TimeLink.class, tLink);
				}
			}
			
			if(mk != null)
				intraSentential.add(mk);
		
		}
	
	}
}
