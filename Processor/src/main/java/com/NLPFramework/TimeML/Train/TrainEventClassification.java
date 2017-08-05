package com.NLPFramework.TimeML.Train;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Files.XMLFile;
import com.NLPFramework.Formatters.EventClassikAnnotatedFormatter;
import com.NLPFramework.Formatters.EventClassikFormatter;
import com.NLPFramework.Formatters.FeaturesEventAnnotatedFormatter;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.Processor.Annotator;
import com.NLPFramework.Processor.Classification;
import com.NLPFramework.Processor.EventProcessing;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.NLPProcessor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.Processor.TemporalRelationProcessing;
import com.NLPFramework.Processor.TimexProcessing;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Test.Score;
import com.NLPFramework.TimeML.Test.Scorer;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;

public class TrainEventClassification extends TrainTMLBase implements IActionExecutor {


	private String elem = "event";
	protected String filter;
	public TrainEventClassification(File train_dir, File modelDir, String approach,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, lang, strategy);
		this.filter = filter;
	}
	
	public TrainEventClassification(File train_dir, File modelDir, String approach,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, lang, method);
		this.filter = filter;
	}


	@Override
	public void execute()
	{
		ISentenceFormatter formatter = new EventClassikAnnotatedFormatter();
		train(formatter, strategy.getEventProcessing().getRecognition(), filter);
	}
	
	@Override
	public String getElement() {
		// TODO Auto-generated method stub
		return "event";
	}

	@Override
	public TimeMLTrainTypes getType() {
		// TODO Auto-generated method stub
		return TimeMLTrainTypes.classification;
	}
	
}
