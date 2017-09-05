package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.TimeML.EventSubEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventSubEventRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestEventSubEventClassification extends TestTMLBase implements IActionExecutor {


	private String elem = "tlink-subevent";
	protected String filter;
	
	public TestEventSubEventClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestEventSubEventClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, method);
		this.filter = filter;
	}

	@Override
	public void execute()
	{
		String output = "", key;
		Scorer scorer = new Scorer();
		try 
		{

			TokenizedFileHashtable featuresTestFiles = getFiles();

			File fileClassikAnnotated = featuresTestFiles.toFile(new EventSubEventRelationAnnotatedFormatter(), getFeaturesTestDir() + File.separator + elem + "ClassikFeatures.pipes");
			
			key = fileClassikAnnotated.getAbsolutePath();

			TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
			TokenizedFileHashtable featuresTestKeyFiles = getFiles();
			Logger.WriteDebug(String.valueOf(featuresTestFiles.keySet().size()));
			for(String fileName : featuresTestFiles.keySet())
			{	
				Logger.WriteDebug(fileName);
				TokenizedFile kFileTemp = featuresTestKeyFiles.get(fileName);
				TimeMLFile kTmlFile = (TimeMLFile)(kFileTemp);
				
				TokenizedFile fTemp = annotatedTestFiles.get(fileName);
				TimeMLFile f = (TimeMLFile)(fTemp);

				if(f.getAnnotations(TimeLink.class) != null)
				{
					for(Object annotation : f.getAnnotations(TimeLink.class))
					{
						TimeLink tl = (TimeLink) annotation;
						MakeInstance mkInstance = tl.eventInstance;
						MakeInstance mkRelatedInstance = tl.relatedToEventInstance;
						if(mkInstance == null || mkRelatedInstance == null)
							continue;
						
						if(TimeMLHelper.areWordsInSameSentence(f, mkInstance.event.word, mkRelatedInstance.event.word))
						tl.type = null;
					}
					
				}
				strategy.getTemporalRelationProcessing().getSubordinate_events().Test(f, modelDir.getAbsolutePath(),approach, "classification_e-sube", f.getLanguage(), new EventSubEventRelationFormatter(), new EventSubEventRelationAnnotatedFormatter());
				Score score = new Score(elem, f.getName());
				scorer.scoreTimeLinkEventEventClassification(score, f, kTmlFile);
				//"VB(?:D|N|P|G|Z)"
				score.print("total");

			}
 
			Score score =  scorer.score(elem, "classification_e-sube", annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");

		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
		}
	}
	
}
