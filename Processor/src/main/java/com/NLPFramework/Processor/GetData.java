package com.NLPFramework.Processor;

import java.io.File;
import java.util.Iterator;
import java.util.Map;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.TimeML.Domain.TimeMLFile;

public class GetData implements IActionExecutor {

	private File dataPath = null;
	public GetData(String path)
	{
		dataPath = new File(path);
	}
	
	public GetData(File path)
	{
		dataPath = path;
	}
	
	@Override
	public void execute() throws Exception 
	{
		String featuresTrainDir = dataPath.getParent() + File.separator + dataPath.getName() + "_features";
		String featuresTrainFilePath = featuresTrainDir + File.separator + "features.obj";
		// Check for features files (train/test)
		if ( !new File(featuresTrainFilePath).exists()) 
		{
			FileConverter.tmldir2features(dataPath, "SemEval4");
		}
		
		TokenizedFileHashtable files = new TokenizedFileHashtable(featuresTrainFilePath);
		
		Iterator<Map.Entry<String,TokenizedFile>> it = files.entrySet().iterator();
		//TODO: extract timeLine
		while(it.hasNext())
		{
			TokenizedFile file = it.next().getValue();
			NLPProcessor processor = new NLPProcessor(file);
			processor.classifyEvents();
			processor.setMakeInstancesFromEvents();
			processor.processTimex();
			processor.setIds();
			processor.RecognizeTLINKS();
			TimeMLFile timeMLFile = new TimeMLFile(processor.getFile());
		//	ret = processor.getTimeMLFile().toTML(timeMLFile);
		}
		
		

	}

}
