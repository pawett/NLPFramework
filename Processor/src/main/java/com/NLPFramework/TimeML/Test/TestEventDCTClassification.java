package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Formatters.TimeML.EventDCTRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventDCTRelationFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationAnnotatedFormatter;
import com.NLPFramework.Formatters.TimeML.EventTimexRelationFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestEventDCTClassification extends TestTMLFileFormatterBase implements IActionExecutor {


	protected String filter;
	
	public TestEventDCTClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, strategy);
		this.filter = filter;
	}
	
	public TestEventDCTClassification(File train_dir,  File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method, String filter)
	{
		super(train_dir, modelDir, approach, task, lang, method);
		this.filter = filter;
	}
	
	@Override
	public String getElem()
	{
		return "e-dct";
	}
	
	@Override
	public IFileFormatter getFormatter()
	{
		return new EventDCTRelationFormatter();
	}
	@Override
	public IFileFormatter getAnnotatedFormatter()
	{
		return new EventDCTRelationAnnotatedFormatter();
	}
	
	@Override
	public void cleanAnnotations(TimeMLFile file) 
	{

		if(file.getAnnotations(TimeLink.class) != null)
		{
			for(Object annotation : file.getAnnotations(TimeLink.class))
			{
				TimeLink tl = (TimeLink) annotation;
				tl.type = null;
			}

		}
	}
	
	@Override
	public void scoreFile(TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), annotatedFile.getName());
		scorer.scoreTimeLinkDCTClassification(score, annotatedFile, keyFile);
		//"VB(?:D|N|P|G|Z)"
		score.print("total");
	}
	
	@Override
	public void scoreAllFiles(TokenizedFileHashtable annotatedFilePath, TokenizedFileHashtable keyFilePath)
	{
		Scorer scorer = new Scorer();
		Score score = new Score(getElem(), getElem());
		score =  scorer.score(getElem(), task + "_" + getElem(), annotatedFilePath, keyFilePath, null);
		score.print("total");
		
		/*score = scorer.score_class(annotatedFilePath, keyFilePath, -1);
		//score.print("attribs");
		score.print("total");*/
	}
	
	@Override
	public IMachineLearningMethod getMLMethod()
	{
		return strategy.getTemporalRelationProcessing().getEvent_DCT();
	}

/*	@Override
	public void execute()
	{
		String output = "", key;
		Scorer scorer = new Scorer();
		try 
		{

			TokenizedFileHashtable featuresTestFiles = getFiles();

			File fileClassikAnnotated = featuresTestFiles.toFile(new EventDCTRelationAnnotatedFormatter(), getFeaturesTestDir() + File.separator + getElem() + "ClassikFeatures.pipes");
			
			key = fileClassikAnnotated.getAbsolutePath();

			TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
			TokenizedFileHashtable featuresTestKeyFiles = getFiles();
			Logger.WriteDebug(String.valueOf(featuresTestFiles.keySet().size()));
			for(String fileName : featuresTestFiles.keySet())
			{	
				Logger.WriteDebug(fileName);
				TokenizedFile kFileTemp = featuresTestKeyFiles.get(fileName);
				TimeMLFile kTmlFile = new TimeMLFile(kFileTemp);
				
				TokenizedFile fTemp = annotatedTestFiles.get(fileName);
				TimeMLFile f = new TimeMLFile(fTemp);

				if(f.annotations.get(TimeLink.class) != null)
				{
					for(Object annotation : f.annotations.get(TimeLink.class))
					{
						TimeLink tl = (TimeLink) annotation;
						tl.type = null;
					}
					
				}
				strategy.getTemporalRelationProcessing().getEvent_DCT().Test(f, modelDir.getAbsolutePath(),approach, "classification_e-dct", f.getLanguage(), new EventDCTRelationFormatter(), new EventDCTRelationAnnotatedFormatter());
				Score score = new Score(getElem(), f.getName());
				scorer.scoreTimeLinkDCTClassification(score, f, kTmlFile);
				//"VB(?:D|N|P|G|Z)"
				score.print("total");

			}
 

			Score score =  scorer.score(getElem(), "classification_e-dct", annotatedTestFiles, featuresTestKeyFiles, null);
			score.print("total");

		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
		}
	}*/
	
}
