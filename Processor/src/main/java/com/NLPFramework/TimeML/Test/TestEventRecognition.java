package com.NLPFramework.TimeML.Test;

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
import com.NLPFramework.TimeML.Train.TimeMLTrainTypes;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;

public class TestEventRecognition extends TestTMLBase implements IActionExecutor {


	private String elem = "event";
	protected String filter;
	
	public TestEventRecognition(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestEventRecognition(File train_dir,  File modelDir, String approach, String task,
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
		return new FeaturesFormatter();
	}
	@Override
	public ISentenceFormatter getAnnotatedFormatter()
	{
		return new FeaturesEventAnnotatedFormatter();
	}
	
	@Override
	public void cleanAnnotations(TimeMLFile file) {
		file.cleanAnnotations(TimeMLTrainTypes.recognition);
	}
	
	@Override
	public void scoreFile(TokenizedFile annotatedFile, TokenizedFile keyFile)
	{
		Logger.WriteDebug(annotatedFile.getName());
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), annotatedFile.getName());
		scorer.scoreRecognition(getElem(), null, score, annotatedFile, keyFile);
		//"VB(?:D|N|P|G|Z)"
		score.print("total");
	}
	
	@Override
	public void scoreAllFiles(String annotatedFilePath, String keyFilePath)
	{
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), getElem());
		
		score =  scorer.score(elem, "recognition", getAnnotatedFiles(), getKeyFiles(), null);
		score.print("total");
		
		Logger.WriteDebug("VERB " + elem);
		score =  scorer.score(elem, "recognition", getAnnotatedFiles(), getKeyFiles(), "VB");
		score.print("total");
		
		Logger.WriteDebug("ADJ " + elem);
		score =  scorer.score(elem, "recognition", getAnnotatedFiles(), getKeyFiles(), "JJ");
		score.print("total");
		Logger.WriteDebug("NOUN " + elem);
		score =  scorer.score(elem, "recognition", getAnnotatedFiles(), getKeyFiles(), "NN");
		score.print("total");
		Logger.WriteDebug("TOTAL " + elem);
		score =  scorer.score(elem, "recognition", getAnnotatedFiles(), getKeyFiles(), null);
		score.print("total");
	}
	@Override
	public void test(TokenizedFile f)
	{
		strategy.getEventProcessing().getRecognition().Test(f, modelDir.getAbsolutePath(),approach, "recognition_event_VB", f.getLanguage(), getFormatter(), getAnnotatedFormatter());
		strategy.getEventProcessing().getRecognition().Test(f, modelDir.getAbsolutePath(),approach, "recognition_event_NN", f.getLanguage(), getFormatter(), getAnnotatedFormatter());
		strategy.getEventProcessing().getRecognition().Test(f, modelDir.getAbsolutePath(),approach, "recognition_event_JJ", f.getLanguage(), getFormatter(), getAnnotatedFormatter());
	}
	
	@Override
	public IMachineLearningMethod getMLMethod()
	{
		return strategy.getEventProcessing().getRecognition();
	}
	
}
