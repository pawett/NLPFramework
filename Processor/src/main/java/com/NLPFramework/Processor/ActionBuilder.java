package com.NLPFramework.Processor;

import java.io.File;
import java.util.ArrayList;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Formatters.FeaturesFormatter;
import com.NLPFramework.Formatters.TokenFileFormatter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.FileUtils;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;


public class ActionBuilder 
{
	public static String localDatasetPath = FileUtils.getApplicationPath() + "program-data" + File.separator + "TIMEE-training" + File.separator;

	public static IActionExecutor getAction(ArrayList<String> files, String action_parameters) throws Exception
	{
		
		IActionExecutor actionExecutor = null;
		IMachineLearningMethod mlmethod;
		 String paramMethod = getParameter(action_parameters, "ML");
		if (paramMethod == null || paramMethod.equalsIgnoreCase("crf")) {
			Configuration.setMachineLearningMethod(new CRF());
		}else
		{
			Configuration.setMachineLearningMethod(new SVM());
		}
		
		String approach = getParameter(action_parameters, "approach");
		if (approach == null) {
		    approach = "TIPSemB";
		}
		
		String task = getParameter(action_parameters, "task");
		if (task == null) {
		    task = "recognition";
		}
		
		String element = getParameter(action_parameters, "element");
		if (element == null) {
		    element = "timex";
		}
		
		String strategy = getParameter(action_parameters, "strategy");
		if (strategy == null) {
		    strategy = "normal";
		}
		boolean rebuild_dataset = false;
		if (getParameter(action_parameters, "rebuild_dataset") != null)
		{
		
			rebuild_dataset = Boolean.parseBoolean(getParameter(action_parameters, "rebuild_dataset"));
		}
		
		
		String traind = getParameter(action_parameters, "train_dir");
		File train_dir = null;
		if (traind == null) {
		    train_dir = FileHelper.GetFileAndCreateDir(localDatasetPath + Configuration.getLanguage() + "/train_tml");
		} else {
		    train_dir = FileHelper.GetFileAndCreateDir(traind);
		    if (!train_dir.exists() || !train_dir.isDirectory()) {
		        throw new Exception("Input " + traind + " does not exist or is not a directory.");
		    }
		}
		
		String testd = getParameter(action_parameters, "test_dir");
		File test_dir = null;
		
		if (testd == null) {
		    test_dir = FileHelper.GetFileAndCreateDir(localDatasetPath + Configuration.getLanguage() + "/test_tml");
		} else {
		    test_dir = FileHelper.GetFileAndCreateDir(testd);
		    if (!test_dir.exists() || !test_dir.isDirectory()) {
		        throw new Exception("Input " + testd + " does not exist or is not a directory.");
		    }
		}
			    
		
		switch(Configuration.getAction())
		{
			
			case SRL:
				NLPProcessor processor = new NLPProcessor(FileHelper.getFilesFromPath(files).get(0).getAbsolutePath(), null);
				TokenizedFile file = processor.getFeatures();
				TokenFileFormatter formatter = new TokenFileFormatter(file);
				Logger.Write(formatter.toString(new FeaturesFormatter()));
				break;
			case TEST:
				break;
			case TRAIN:
				actionExecutor = new TrainAction(task, element, train_dir, approach, Configuration.getLanguage(), rebuild_dataset, Configuration.getMachineLearningMethod());
				break;
			case TRAIN_AND_TEST:
				actionExecutor = new TrainAction(task, element, train_dir, approach, Configuration.getLanguage(), rebuild_dataset, Configuration.getMachineLearningMethod());
				actionExecutor.execute();
				actionExecutor = new TestAction(task, element, test_dir, approach, Configuration.getLanguage(), rebuild_dataset, Configuration.getMachineLearningMethod());
				break;
			case GETDATA:
				actionExecutor = new GetData(Configuration.getFolderPath());
				break;
			default:
			case ANNOTATE:
				Configuration.setDCT(getParameter(action_parameters, "DCT"));
				actionExecutor = new AnnotateAction(FileHelper.getFilesFromPath(files));
				break;
		}
		
		return actionExecutor;
	}
	
	  public static String getParameter(String actionParameters, String param) {
	        String paramValue = null;
	        if (actionParameters != null && actionParameters.contains(param)) {
	            if (actionParameters.matches(".*" + param + "=[^,]*,.*")) {
	                paramValue = actionParameters.substring(actionParameters.lastIndexOf(param + "=") + param.length() + 1, actionParameters.indexOf(',', actionParameters.lastIndexOf(param + "=")));
	            } else {
	                if (actionParameters.matches(".*" + param + "=[^,]*")) {
	                    paramValue = actionParameters.substring(actionParameters.lastIndexOf(param + "=") + param.length() + 1);
	                }
	            }
	        }
	        return paramValue;
	    }
}
