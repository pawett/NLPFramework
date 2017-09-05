package com.NLPFramework.TimeML.Test;

import java.io.File;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Processor.IActionExecutor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TestTMLFileFormatterBase extends TestBase implements IActionExecutor {

	public TestTMLFileFormatterBase(File test_dir, File modelDir, String approach, String task,
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
	
	public TestTMLFileFormatterBase(File test_dir,  File modelDir, String approach, String task,
			Language lang, 
			IMachineLearningMethod method)
	{
		super(test_dir, modelDir, approach, task, lang, method);
	}
	
	public IFileFormatter getFormatter()
	{
		return null;
	}
	
	public IFileFormatter getAnnotatedFormatter()
	{
		return null;
	}
	
	public void cleanAnnotations(TimeMLFile file)
	{
		
	}
	
	public void scoreFile(TimeMLFile annotatedFile, TimeMLFile keyFile)
	{
		
	}
	
	public void scoreAllFiles(TokenizedFileHashtable annotatedFilePath, TokenizedFileHashtable keyFilePath)
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

		File fileClassikAnnotated = featuresTestFiles.toFile(getAnnotatedFormatter(), getFeaturesTestDir() + File.separator + getElem() + task + "key.pipes");
		
		key = fileClassikAnnotated.getAbsolutePath();

		TokenizedFileHashtable annotatedTestFiles = featuresTestFiles;
		TokenizedFileHashtable featuresTestKeyFiles = getFiles();
		Logger.WriteDebug(String.valueOf(featuresTestFiles.keySet().size()));
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
		
		
		fileClassikAnnotated = annotatedTestFiles.toFile(getAnnotatedFormatter(), getFeaturesTestDir() + File.separator + getElem() + task + "annotated.pipes");

		/*String annot = null;
		String model = modelDir + File.separator + approach + "_" + task  + "_" + getElem() + "_" + lang + "." + getMLMethod().getClass().getSimpleName() + "model";
		annot = getMLMethod().Test(fileClassik.getAbsolutePath(), model);*/
		scoreAllFiles(annotatedTestFiles, featuresTestKeyFiles);

		
	} catch (Exception e) {
		Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e); 
	}
	
}
	
}
