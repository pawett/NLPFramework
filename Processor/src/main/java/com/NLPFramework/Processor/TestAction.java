package com.NLPFramework.Processor;

import java.io.File;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Formatters.TimeML.FeaturesEventAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.FeaturesTimexAnnotatedFormatter;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.TimeML.Test.TestBase;
import com.NLPFramework.TimeML.Test.TestEventClassification;
import com.NLPFramework.TimeML.Test.TestEventDCTClassification;
import com.NLPFramework.TimeML.Test.TestEventEventClassification;
import com.NLPFramework.TimeML.Test.TestEventRecognition;
import com.NLPFramework.TimeML.Test.TestEventSubEventClassification;
import com.NLPFramework.TimeML.Test.TestEventTimexClassification;
import com.NLPFramework.TimeML.Test.TestTimexClassification;
import com.NLPFramework.TimeML.Test.TestTimexRecognition;
import com.NLPFramework.TimeML.Train.TrainBase;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;


public class TestAction implements IActionExecutor 
{

	private File trainDir;
	private File testDir;
	private String approach;
	private Language lang;
	private boolean rebuildDataSet;
	private String element;
	private String task;
	IMachineLearningMethod mlMethod;
	
	public TestAction(String task, String element, File test_dir, String approach, Language lang, boolean rebuild_database, IMachineLearningMethod mlMethod)
	{
		testDir = test_dir;
		this.approach = approach;
		this.lang = lang;
		this.rebuildDataSet = rebuild_database;	
		this.element = element;
		this.task = task;
		this.mlMethod = mlMethod;
	}
	
	@Override
	public void execute() throws Exception 
	{
		
		String featuresTestDir = testDir.getParent() + File.separator + testDir.getName() + "_" + approach + "_features";
		String featuresTestFilePath = featuresTestDir + File.separator + "features.obj";
		
		if (rebuildDataSet || !new File(featuresTestFilePath).exists())
		{
			TokenizedFileHashtable testFiles = TMLExtractor.getAnnotationsFromDir(testDir);
			String featuresDir = testDir.getParent() + File.separator + testDir.getName() + "_" + approach + "_features" + File.separator;
			FileHelper.saveFilesAsBinary(testFiles, featuresDir);
		}
		
		TestBase testModel = null;

		File modelDir = FileHelper.GetFileAndCreateDir(testDir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);
		
		
		switch(element.toLowerCase())
		{
			case "event":
				switch(task.toLowerCase())
				{
				case "recognition":
					testModel = new TestEventRecognition(testDir, modelDir, approach, task, lang, mlMethod, null);
					testModel.execute();
					break;
				case "classification":
					testModel = new TestEventClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
					testModel.execute();
					break;
				}
				break;
			case "timex":
				switch(task.toLowerCase())
				{
					case "recognition":
						testModel = new TestTimexRecognition(testDir, modelDir, approach, task,lang, mlMethod, null);
						testModel.execute();
						break;
					case "classification":
						testModel = new TestTimexClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
						testModel.execute();
						break;
				}
				break;
			case "tlink":
				switch(task.toLowerCase())
				{
					case "classification":
						testModel = new TestEventTimexClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
						testModel.execute();
						break;
				}
				break;
				
			case "tlink-dct":
				switch(task.toLowerCase())
				{
					case "classification":
						testModel = new TestEventDCTClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
						testModel.execute();
						break;
				}
				break;
			case "tlink-event":
				switch(task.toLowerCase())
				{
					case "classification":
						testModel = new TestEventEventClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
						testModel.execute();
						break;
				}
				break;
			case "tlink-subevent":
				switch(task.toLowerCase())
				{
					case "classification":
						testModel = new TestEventSubEventClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
						testModel.execute();
						break;
				}
				break;
				
			case "all":
				
				testModel = new TestEventRecognition(testDir, modelDir, approach, task, lang, mlMethod, null);
				testModel.execute();
				
				mlMethod = new SVM();

				testModel = new TestEventClassification(testDir, modelDir, approach,task, lang, mlMethod, null);
				testModel.execute();
				

				testModel = new TestEventTimexClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
				testModel.execute();
				
	
				testModel = new TestEventDCTClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
				testModel.execute();
				
		
				testModel = new TestEventEventClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
				testModel.execute();
				
		
				testModel = new TestEventSubEventClassification(testDir, modelDir, approach, task, lang, mlMethod, null);
				testModel.execute();
		}
		
		

	/*	
		switch(task.toLowerCase())
		{
		case "recognition":
			if(element.equalsIgnoreCase("event")){
				train(trainModel, dir, "VB");
				train(trainModel, dir, "NN");
				train(trainModel, dir, "JJ");
			}else
				train(trainModel, dir, null);
			trainModel.recognition(dir);
			break;
		case "classification":
			trainModel.classification(dir);
			break;
		case "normalization":
			trainModel.normalizationType("timex");
			break;
		case "categorization":
			trainModel.categorization(element.toLowerCase());
			break;
		case "idcat":
		//	trainModel.idcat_tml(strategy);
			break;
		case "all":
		default:
		//	trainModel.full_tml();
			break;
		}	
*/
	}
	
	 private void train(TrainBase trainModel, File dir,String filter)
	    {
	    	String model = null;
			ISentenceFormatter formatter = null;
			if(element.toLowerCase().equalsIgnoreCase("event"))
				formatter = new FeaturesEventAnnotatedFormatter(filter);
			
			if(element.toLowerCase().equalsIgnoreCase("timex"))
				formatter = new FeaturesTimexAnnotatedFormatter();
				
			
			trainModel.train("_rec_", dir, formatter, filter);
	    }
}
