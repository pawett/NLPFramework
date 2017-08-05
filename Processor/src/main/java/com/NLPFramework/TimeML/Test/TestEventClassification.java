package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.EventClassikAnnotatedFormatter;
import com.NLPFramework.Formatters.EventClassikFormatter;
import com.NLPFramework.Formatters.FeaturesEventAnnotatedFormatter;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.NLPProcessor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Train.TimeMLTrainTypes;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestEventClassification extends TestTMLBase implements IActionExecutor {

	protected String filter;
	
	public TestEventClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestEventClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, method);
		this.filter = filter;
	}
	
	@Override
	public String getElem()
	{
		return "event";
	}
	
	@Override
	public ISentenceFormatter getFormatter()
	{
		return new EventClassikFormatter();
	}
	@Override
	public ISentenceFormatter getAnnotatedFormatter()
	{
		return new EventClassikAnnotatedFormatter();
	}
	
	@Override
	public void cleanAnnotations(TimeMLFile file) {
		file.cleanAnnotations(TimeMLTrainTypes.classification);
	}
	
	@Override
	public void scoreFile(TokenizedFile annotatedFile, TokenizedFile keyFile)
	{
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), annotatedFile.getName());
		scorer.scoreClassification(getElem(), null, score, annotatedFile, keyFile);
		//"VB(?:D|N|P|G|Z)"
		score.print("total");
	}
	
	@Override
	public void scoreAllFiles(String annotatedFilePath, String keyFilePath)
	{
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), getElem());
		
		score =  scorer.score(getElem(), "classification", getAnnotatedFiles(), getKeyFiles(), null);
		score.print("total");

		Logger.WriteDebug("VERB " + getElem());
		score =  scorer.score(getElem(), "classification", getAnnotatedFiles(), getKeyFiles(), "VB");
		score.print("total");

		Logger.WriteDebug("ADJ " + getElem());
		score =  scorer.score(getElem(), "classification", getAnnotatedFiles(), getKeyFiles(), "JJ");
		score.print("total");
		Logger.WriteDebug("NOUN " + getElem());
		score =  scorer.score(getElem(), "classification", getAnnotatedFiles(), getKeyFiles(), "NN");
		score.print("total");
		Logger.WriteDebug("TOTAL " + getElem());
		score =  scorer.score(getElem(), "classification", getAnnotatedFiles(), getKeyFiles(), null);
		score.print("total");
		
		
		/*score = scorer.score_class(annotatedFilePath, keyFilePath, -1);
		//score.print("attribs");
		score.print("total");*/
	}
	
	@Override
	public IMachineLearningMethod getMLMethod()
	{
		return strategy.getEventProcessing().getClassification();
	}
	
}
