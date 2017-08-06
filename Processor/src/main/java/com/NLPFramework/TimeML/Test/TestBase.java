package com.NLPFramework.TimeML.Test;

import java.io.File;
import java.util.HashMap;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.Processor.EventProcessing;
import com.NLPFramework.Processor.FeatureExtractorEnglish;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.Processor.TemporalRelationProcessing;
import com.NLPFramework.Processor.TimexProcessing;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestBase implements IActionExecutor {

	protected File test_dir;
	protected String approach;
	protected Language lang;
	protected TemporalInformationProcessingStrategy strategy;
	protected File modelDir;
	protected String task;

	
	public TestBase(File test_dir, File modelDir, String approach, String task,
			Language lang, 
			TemporalInformationProcessingStrategy strategy)
	{
		this.test_dir = test_dir;
		this.approach = approach;
		this.lang = lang;
		this.strategy = strategy;
		this.modelDir = modelDir;
		this.task = task;

	}
	
	public TestBase(File test_dir, File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method)
	{
		this.test_dir = test_dir;
		this.modelDir = modelDir;
		this.approach = approach;
		this.lang = lang;
		this.task = task;
		this.strategy = new TemporalInformationProcessingStrategy();
		TimexProcessing timexP = new TimexProcessing();
		timexP.setClassification(method);
		timexP.setNormalization(method);
		timexP.setRecognition(method);
		strategy.setTimexProcessing(timexP);
		
		EventProcessing eventP = new EventProcessing();
		eventP.setClassification(method);
		eventP.setRecognition(method);
		strategy.setEventProcessing(eventP);
		
		TemporalRelationProcessing temporalRelationP = new TemporalRelationProcessing();
		temporalRelationP.setEvent_DCT(method);
		temporalRelationP.setEvent_timex(method);
		temporalRelationP.setMain_events(method);
		temporalRelationP.setSubordinate_events(method);
		strategy.setTemporalRelationProcessing(temporalRelationP);
		

	}
	
	public String getFeaturesTestDir()
	{
		return test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features";
	}
	public String getFeaturesTestPath()
	{
		return getFeaturesTestDir() + File.separator + "features.obj";
	}
	
	public TokenizedFileHashtable getFiles()
	{
		String featuresTestPath = getFeaturesTestPath();
		TokenizedFileHashtable files = FileHelper.getBinaryFiles(featuresTestPath);

	/*	files.keySet().parallelStream().forEach((fileName) -> {
			TokenizedFile kFileTemp = files.get(fileName);
			
			FeatureExtractorEnglish featureExtractor = new FeatureExtractorEnglish();
			featureExtractor.setFeatures(kFileTemp);
		});*/
		
		return files;
	}

	public  String localDatasetPath = FileUtils.getApplicationPath() + "program-data/TIMEE-training/";
	public  HashMap<String, String> category_files = new HashMap<String, String>() {

		{
			put("e-t", "base-segmentation.e-t-link-features");
			put("e-dct", "base-segmentation.e-dct-link-features");
			put("e-main", "base-segmentation.e-main-link-features");
			put("e-sub", "base-segmentation.e-sub-link-features");
		}
	};
	@Override
	public void execute() throws Exception {
		// TODO Auto-generated method stub
		
	}
}
