package com.NLPFramework.Processor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.FileHelper;
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
		String featuresTrainDir = dataPath.getParent() + File.separator + dataPath.getName() + "_" + "SemEval4" + "_features";
		String featuresTrainFilePath = featuresTrainDir + File.separator + "features.obj";
		TokenizedFileHashtable files = new TokenizedFileHashtable();
		// Check for features files (train/test)
		if (!new File(featuresTrainFilePath).exists()) 
		{
			files = FileConverter.tmldir2features(dataPath, "SemEval4");
			Iterator<Map.Entry<String,TokenizedFile>> it = files.entrySet().iterator();
			//TODO: extract timeLine
			while(it.hasNext())
			{
				TokenizedFile file = it.next().getValue();
				NLPProcessor processor = new NLPProcessor(file);
				processor.executeCoreference();
				processor.classifyEvents();
				processor.setMakeInstancesFromEvents();
				processor.processTimex();
				processor.setIds();
				processor.RecognizeTLINKS();
				
				TimeMLFile timeMLFile = new TimeMLFile(processor.getFile());
			//	ret = processor.getTimeMLFile().toTML(timeMLFile);
			}
			
			FileHelper.saveFilesAsBinary(files, featuresTrainDir);
			
		}else
		{
			files = FileHelper.getBinaryFiles(featuresTrainFilePath);
		}
		
		Iterator<Map.Entry<String,TokenizedFile>> it = files.entrySet().iterator();
		//TODO: extract timeLine
		while(it.hasNext())
		{
			TokenizedFile file = it.next().getValue();
			TimeMLFile timeMlFile = new TimeMLFile(file);
			TimeLineProcessor tlProcessor = new TimeLineProcessor(timeMlFile);
			tlProcessor.execute();
		}
		
		
		
		//createTimeLine
		

	}

}
