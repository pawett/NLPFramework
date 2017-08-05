package com.NLPFramework.TimeML.Train;

import java.io.File;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Formatters.EventSubEventRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TrainEventSubEventClassification extends TrainTMLBase implements IActionExecutor {


	protected String filter;
	public TrainEventSubEventClassification(File train_dir, File modelDir, String approach,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, lang, strategy);
		this.filter = filter;
	}
	
	public TrainEventSubEventClassification(File train_dir, File modelDir, String approach,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, lang, method);
		this.filter = filter;
	}


	@Override
	public void execute()
	{
		IFileFormatter formatter = new EventSubEventRelationAnnotatedFormatter();
		train(formatter, strategy.getTemporalRelationProcessing().getMain_events(), filter);
	}
	
	@Override
	public String getElement() {
		// TODO Auto-generated method stub
		return "e-sube";
	}

	@Override
	public TimeMLTrainTypes getType() {
		// TODO Auto-generated method stub
		return TimeMLTrainTypes.classification;
	}
	
}
