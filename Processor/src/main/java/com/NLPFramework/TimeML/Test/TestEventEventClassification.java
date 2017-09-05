package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.TimeML.EventEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventEventRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationFormatter;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestEventEventClassification extends TestTMLBase implements IActionExecutor {


	private String elem = "tlink-event";
	protected String filter;
	
	public TestEventEventClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestEventEventClassification(File train_dir,  File modelDir, String approach, String task,
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

			File fileClassikAnnotated = featuresTestFiles.toFile(new EventTimexRelationAnnotatedFormatter(), getFeaturesTestDir() + File.separator + elem + "ClassikFeatures.pipes");
			
			key = fileClassikAnnotated.getAbsolutePath();

			TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
			TokenizedFileHashtable featuresTestKeyFiles = getFiles();
		
			Logger.WriteDebug(String.valueOf(featuresTestFiles.keySet().size()));
			for(String fileName : featuresTestFiles.keySet())
			{	
				Logger.WriteDebug(fileName);
				TokenizedFile kFileTemp = featuresTestKeyFiles.get(fileName);
				TimeMLFile kTmlFile = (TimeMLFile)kFileTemp;// new TimeMLFile(kFileTemp);
				
				TokenizedFile fTemp = annotatedTestFiles.get(fileName);
				TimeMLFile f = (TimeMLFile)fTemp;

				
				strategy.getTemporalRelationProcessing().getMain_events().Test(f, modelDir.getAbsolutePath(),approach, "classification_e-e", f.getLanguage(), new EventEventRelationFormatter(), new EventEventRelationAnnotatedFormatter());
				Score score = new Score(elem, f.getName());
				scorer.scoreTimeLinkEventEventClassification(score, f, kTmlFile);
				//"VB(?:D|N|P|G|Z)"
				score.print("total");

			}
 
			//AnnotScore results
		//	TokenizedFileHashtable featuresTestKeyFiles = new TokenizedFileHashtable(featuresTestPath);

			Score score =  scorer.score(elem, "classification_e-e", annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");

		/*	Logger.WriteDebug("VERB " + elem);
			score =  scorer.score(elem, "classification", annotatedTestFiles, featuresTestKeyFiles, "VB");
			score.print("total");

			Logger.WriteDebug("ADJ " + elem);
			score =  scorer.score(elem, "classification", annotatedTestFiles, featuresTestKeyFiles, "JJ");
			score.print("total");
			Logger.WriteDebug("NOUN " + elem);
			score =  scorer.score(elem, "classification", annotatedTestFiles, featuresTestKeyFiles, "NN");
			score.print("total");
			Logger.WriteDebug("TOTAL " + elem);
			score =  scorer.score(elem, "classification", annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");
			String annot = null;
			String model = modelDir + File.separator + approach + "_" + "classification" + "_" + "event_" + lang + "." + strategy.getEventProcessing().getClassification().getClass().getSimpleName() + "model";
			annot = strategy.getEventProcessing().getClassification().Test(fileClassik.getAbsolutePath(), model);
			score = scorer.score_class(annot, key, -1);
			//score.print("attribs");
			score.print("total");
			*/
			/*
			TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
			TokenizedFileHashtable featuresTestKeyFiles = new TokenizedFileHashtable(featuresTestPath);
			String annot = null;
			String model = modelDir + File.separator + approach + "_" + "class" + "_" + "event_" + lang + "." + strategy.getEventProcessing().getClassification().getClass().getSimpleName() + "model";
			annot = strategy.getEventProcessing().getClassification().Test(fileClassik.getAbsolutePath(), model);

			featuresTestFiles = new TokenizedFileHashtable(featuresTestPath);
			Score score =  scorer.score(elem, "classification", annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");
			 score = scorer.score_class(annot, key, -1);
			 score.print("total");
			/*TimeMLFile f = new TimeMLFile(featuresTestFiles);
			strategy.getEventProcessing().getRecognition().Test(f, modelDir.getAbsolutePath(),approach, "class", lang, new EventClassikFormatter(), new EventClassikAnnotatedFormatter());
			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);

			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			//score.print("attribs");
			score.print("detail");
			//score.print(printopts);
			score.print("");
			 */
		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
		}
	}
	
}
