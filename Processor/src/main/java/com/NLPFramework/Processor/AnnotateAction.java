package com.NLPFramework.Processor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.Crosscutting.*;


public class AnnotateAction implements IActionExecutor 
{

	//public static String localDatasetPath = FileUtils.getApplicationPath() + "program-data" + File.separator + "TIMEE-training" + File.separator;
	private ArrayList<File> input_files;


	
	private String task;
	private String element;
	private String strategy;
	private boolean rebuild_dataset;
	private File test_dir;
	private File train_dir;
            
	private ArrayList<IActionExecutor> dependencies;

	public AnnotateAction(ArrayList<File> files) 
	{
		this.input_files = files;
		
	}


	public void execute() throws Exception 
	{
		if (input_files.size() <= 0) 
		{
			throw new Exception("No input files found");
		}

		
		/*String entities = getParameter("entities");
		/*if (entities != null)
		{
			entities=entities.replaceAll("\\s+", "");
			if(!entities.matches("(timex|event|tlink|tlink-rel-only|timex;event|event;timex|timex;event;tlink|event;timex;tlink)[;]?")) 
			{
				throw new Exception("entities must follow (timex|event|tlink|tlink-rel-only|timex;event|event;timex|timex;event;tlink|event;timex;tlink)[;]? pattern. Found: " + entities + ".");
			}
		}else
		{
			entities="timex;event;tlink";
		}*/

		/*String inputf = getParameter("inputf");

		if (inputf == null) 
		{
			if(!entities.matches("(tlink|tlink-rel-only)"))
				inputf = "plain";
			else
				inputf = "te3input-with-entities"; // just for informative purposes
		}

		if (!entities.matches("(tlink|tlink-rel-only)"))
		{
			inputf=inputf.replaceAll("\\s+", "").toLowerCase();
			if(!(inputf.equals("te3input") || inputf.equals("plain")))
				throw new Exception("inputf must be plain (default) or te3input. Found: " + inputf + ".");
		}
		 */


		// consider null DCT for history domain in the future... not now.

		for (File file : input_files) 
		{
			Logger.Write("\n\nFile: " + file + " Language:" + Configuration.getLanguage());
			String output=null;
			Annotator tip = new Annotator(file, Configuration.getApproach(), null, new TemporalInformationProcessingStrategy(Configuration.getLanguage()));
			//if(!entities.matches("(tlink|tlink-rel-only)"))
			output= tip.Annotate();
			//else
			//	output=tip.annotate_links(null);

			// OUTPUT FILES
			//(new File(output)).renameTo(new File(output.substring(0, output.lastIndexOf("_" + Configuration.getApproach() + "_features" + File.separator)) + ".tml"));
			//(new File(dir + "/" + nlpfile.getFile().getName())).renameTo(nlpfile.getFile());
		}
	}



}
