package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.EventClassikAnnotatedFormatter;
import com.NLPFramework.Formatters.EventClassikFormatter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.NLPProcessor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Train.TimeMLTrainTypes;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestTimexRecognition extends TestTMLBase implements IActionExecutor {


	private String elem = "timex";
	protected String filter;
	
	public TestTimexRecognition(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestTimexRecognition(File train_dir,  File modelDir, String approach, String task,
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

			String featuresTestDir = test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features";

			String featuresTestPath = featuresTestDir + File.separator + "features.obj";

			TokenizedFileHashtable featuresTestFiles = FileHelper.getBinaryFiles(featuresTestPath);

			File fileClassikAnnotated = featuresTestFiles.toFile(new EventClassikAnnotatedFormatter(), featuresTestDir + File.separator + elem + "ClassikFeatures.pipes");
			File fileClassik = featuresTestFiles.toFile(new EventClassikFormatter(), featuresTestDir + File.separator + elem + "Classik.pipes" );

			key = fileClassikAnnotated.getAbsolutePath();

			TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
			TokenizedFileHashtable featuresTestKeyFiles = FileHelper.getBinaryFiles(featuresTestPath);
			for(String fileName : featuresTestFiles.keySet())
			{	
				TokenizedFile kFileTemp = featuresTestKeyFiles.get(fileName);
				TimeMLFile kTmlFile = new TimeMLFile(kFileTemp);
				TokenizedFile fTemp = annotatedTestFiles.get(fileName);
				TimeMLFile f = new TimeMLFile(fTemp);

				f.cleanAnnotations(TimeMLTrainTypes.recognition);
				NLPProcessor nlP = new NLPProcessor(f);
				nlP.processTimex();
							
				//numEventsAnnotated += f.getNumTimex();
				Score score = new Score(elem, f.getName());
				scorer.scoreRecognition(elem, null, score, f, kTmlFile);
				//"VB(?:D|N|P|G|Z)"
				score.print("total");//"detail"

			}

			//AnnotScore results
		//	TokenizedFileHashtable featuresTestKeyFiles = new TokenizedFileHashtable(featuresTestPath);

			Score score =  scorer.score(elem, TimeMLTrainTypes.recognition.toString(), annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");

			Logger.WriteDebug("VERB " + elem);
			score =  scorer.score(elem, TimeMLTrainTypes.recognition.toString(), annotatedTestFiles, featuresTestKeyFiles, "VB");
			score.print("total");

			Logger.WriteDebug("ADJ " + elem);
			score =  scorer.score(elem, TimeMLTrainTypes.recognition.toString(), annotatedTestFiles, featuresTestKeyFiles, "JJ");
			score.print("total");
			Logger.WriteDebug("NOUN " + elem);
			score =  scorer.score(elem, TimeMLTrainTypes.recognition.toString(), annotatedTestFiles, featuresTestKeyFiles, "NN");
			score.print("total");
			Logger.WriteDebug("TOTAL " + elem);
			score =  scorer.score(elem, TimeMLTrainTypes.recognition.toString(), annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");
		
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
