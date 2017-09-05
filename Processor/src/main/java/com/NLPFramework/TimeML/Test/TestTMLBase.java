package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestTMLBase extends TestBase implements IActionExecutor {

	public TestTMLBase(File test_dir, File modelDir, String approach, String task,
			Language lang,
			TemporalInformationProcessingStrategy strategy)
	{
		super(test_dir, modelDir, approach, task, lang, strategy);

	}
	
		
	public String getElem()
	{
		return "";
	}
	
	private TokenizedFileHashtable keyFiles;
	private TokenizedFileHashtable annotatedFiles;
	
	public TokenizedFileHashtable getKeyFiles()
	{
		return keyFiles;
	}
	
	public void setKeyFiles(TokenizedFileHashtable values)
	{
		keyFiles = values;
	}
	
	public TokenizedFileHashtable getAnnotatedFiles()
	{
		return annotatedFiles;
	}
	
	public void setAnnotatedFiles(TokenizedFileHashtable values)
	{
		annotatedFiles = values;
	}
	
	public TestTMLBase(File test_dir,  File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method)
	{
		super(test_dir, modelDir, approach, task, lang, method);
	}
	
	public ISentenceFormatter getFormatter()
	{
		return null;
	}
	
	public ISentenceFormatter getAnnotatedFormatter()
	{
		return null;
	}
	
	public void cleanAnnotations(TimeMLFile file)
	{
		
	}
	
	public void scoreFile(TokenizedFile annotatedFile, TokenizedFile keyFile)
	{
		
	}
	
	public void scoreAllFiles(String annotatedFilePath, String keyFilePath)
	{
	
	}
	
	public void test(TokenizedFile f)
	{
	   getMLMethod().Test(f, modelDir.getAbsolutePath(),approach, task + "_" + getElem() , f.getLanguage(), getFormatter(), getAnnotatedFormatter());
	}
	
	public IMachineLearningMethod getMLMethod()
	{
		return new CRF();
	}

@Override
public void execute(){
	
	String output = "", key;
	
	try 
	{

		TokenizedFileHashtable featuresTestFiles = getFiles();
		String featuresTestDir = getFeaturesTestDir();
		
		File fileClassik = featuresTestFiles.toFile(getFormatter(), featuresTestDir + File.separator + getElem() + task + "Key.pipes" );

		File fileClassikAnnotated = featuresTestFiles.toFile(getAnnotatedFormatter(), featuresTestDir + File.separator + getElem() + task + "annotated.pipes");
		key = fileClassikAnnotated.getAbsolutePath();

		TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
		TokenizedFileHashtable featuresTestKeyFiles = getFiles();
		for(String fileName : featuresTestFiles.keySet())
		{	
			TokenizedFile kFileTemp = featuresTestKeyFiles.get(fileName);
			TimeMLFile kTmlFile = (TimeMLFile)(kFileTemp);
			TokenizedFile fTemp = annotatedTestFiles.get(fileName);
			TimeMLFile f = (TimeMLFile)(fTemp);

			cleanAnnotations(f);
			test(f);
			scoreFile(f, kTmlFile);

		}

		setAnnotatedFiles(annotatedTestFiles);
		setKeyFiles(featuresTestKeyFiles);
		
		
		fileClassikAnnotated = annotatedTestFiles.toFile(getAnnotatedFormatter(), featuresTestDir + File.separator + getElem() + task + "annotated.pipes");

		/*String annot = null;
		String model = modelDir + File.separator + approach + "_" + task  + "_" + getElem() + "_" + lang + "." + getMLMethod().getClass().getSimpleName() + "model";
		annot = getMLMethod().Test(fileClassik.getAbsolutePath(), model);*/
		scoreAllFiles(fileClassik.getAbsolutePath(), fileClassikAnnotated.getAbsolutePath());

		
	} catch (Exception e) {
		Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
	}
	
}
	
}
