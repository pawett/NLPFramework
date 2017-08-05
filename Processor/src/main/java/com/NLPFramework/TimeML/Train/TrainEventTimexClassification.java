package com.NLPFramework.TimeML.Train;

import java.io.File;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Formatters.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TrainEventTimexClassification extends TrainTMLBase implements IActionExecutor {


	private String elem = "eventTimex";
	protected String filter;
	public TrainEventTimexClassification(File train_dir, File modelDir, String approach,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, lang, strategy);
		this.filter = filter;
	}
	
	public TrainEventTimexClassification(File train_dir, File modelDir, String approach,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, lang, method);
		this.filter = filter;
	}


	@Override
	public void execute()
	{
		IFileFormatter formatter = new EventTimexRelationAnnotatedFormatter();
		train(formatter, strategy.getTemporalRelationProcessing().getEvent_timex(), filter);
	}
	
	@Override
	public String getElement() {
		// TODO Auto-generated method stub
		return "e-t";
	}

	@Override
	public TimeMLTrainTypes getType() {
		// TODO Auto-generated method stub
		return TimeMLTrainTypes.classification;
	}
	
}
