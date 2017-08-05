package com.NLPFramework.TimeML.Train;

import java.io.File;
import java.util.HashMap;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.Processor.EventProcessing;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.Processor.TemporalRelationProcessing;
import com.NLPFramework.Processor.TimexProcessing;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TrainBase implements ITrainExecutor {

	protected File train_dir;
	protected String approach;
	protected Language lang;
	protected TemporalInformationProcessingStrategy strategy;
	protected File modelDir;

	
	public TrainBase(File train_dir, File modelDir, String approach,
			Language lang, TemporalInformationProcessingStrategy strategy)
	{
		this.train_dir = train_dir;
		this.approach = approach;
		this.lang = lang;
		this.strategy = strategy;
		this.modelDir = modelDir;

	}
	
	public TrainBase(File train_dir, File modelDir, String approach,
			Language lang, IMachineLearningMethod method)
	{
		this.modelDir = modelDir;
		this.train_dir = train_dir;
		this.approach = approach;
		this.lang = lang;
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

	public String train(String type, File dir, ISentenceFormatter formatter, String filter){return null;}

	@Override
	public String getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeMLTrainTypes getType() {
		// TODO Auto-generated method stub
		return null;
	};
}
