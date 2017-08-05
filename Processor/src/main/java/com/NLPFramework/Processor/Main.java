package com.NLPFramework.Processor;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Helpers.FileConverter;
import com.NLPFramework.Helpers.StringUtils;



public class Main 
{
	
	private static Options getOptions()
	{
		Options opt = new Options();
		opt.addOption("f", "folder", true, "Run tipSem under the specified folder.");
		opt.addOption("h", "help", false, "Print this help");
		opt.addOption("l", "lang", true, "Language code (default \"EN\" [English])");
		opt.addOption("a", "action", true, "Action/s to be done (annotate,TAn)");
		opt.addOption("ap", "action_parameters", true, "Optionally actions can have parameters (	,dct=1999-09-01,entities=event)");
		opt.addOption("t", "text", true, "To use text instead of a file (for short texts)");
		opt.addOption("d", "debug", false, "Debug mode: Output errors stack trace (default: disabled)");
		opt.addOption("srl", "Semantic Role Labeling", false, "Print the semantinc role labeling");
		return opt;
	}
    public static void main( String[] args )
    {
    	Language lang = null;
    	//String action = "annotatecrf"; //default action
    	NLPProcessorAction action = NLPProcessorAction.ANNOTATE;
    	String action_parameters = null;
    	String input_files[];
    	ArrayList<String> files = new ArrayList<>();
    	String input_text = null;
    	long startTime = System.currentTimeMillis();

    	try
    	{
    		Options opt = getOptions();

    		PosixParser parser = new PosixParser();
    		CommandLine cl_options = parser.parse(opt, args);

    		input_files = cl_options.getArgs();

    		if(input_files.length > 0)
    		{
    			for(int i=0;i<input_files.length;i++)
    			{
    				files.add(input_files[i]);
    			}
    		}

    		HelpFormatter hf = new HelpFormatter();
    		for (Option option : cl_options.getOptions()) {
    			switch(option.getLongOpt())
    			{
    			case "folder":
    				Configuration.setFolderPath(option.getValue());
    				files = getFolderFiles(Configuration.getFolderPath());

    				break;
    			case "help":
    				hf.printHelp("TIPSem", opt);
    				System.exit(0);
    				break;
    			case "debug":
    				System.setProperty("DEBUG", "true");
    				break;
    			case "lang":
    				Configuration.setLanguage(Language.valueOf(option.getValue().toUpperCase()));
    				break;
    			case "action":					
    				try {
    					Configuration.setAction(NLPProcessorAction.valueOf(option.getValue().toUpperCase()));
    				} catch (Exception e) {
    					String errortext = "\nValid actions are:\n";

    					for (NLPProcessorAction ac : NLPProcessorAction.values()) {
    						errortext += "\t" + ac.name() + "\n";
    					}
    					throw new RuntimeException("\tIlegal action: " + option.getValue().toUpperCase() + "\n" + errortext);
    				}
    				break;
    			case "action_parameters":
    				action_parameters = option.getValue();
    				break;
    			case "text":
    				input_text = option.getValue();
    				files.add(FileConverter.ConvertTextToFile(input_text));
    				break;
    			}

    		}	

    		IActionExecutor actionExecutor = ActionBuilder.getAction(files, action_parameters);
    		actionExecutor.execute();

    		long endTime = System.currentTimeMillis();
    		long sec=(endTime-startTime)/1000;
    		if(sec<60){
    			System.err.println("Done in "+StringUtils.twoDecPosS(sec)+" sec!\n");
    		}else{
    			System.err.println("Done in "+StringUtils.twoDecPosS(sec/60)+" min!\n");
    		}
    		PrintResultFiles(input_files, input_text);

    	}catch (Exception e) {
    		Logger.WriteError("Error in application", e);
    	}
	}
    
	private static ArrayList<String> getFolderFiles(String folderPath) 
	{
		ArrayList<String> files = new ArrayList<>();
		Path dir = Paths.get(folderPath);
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
		    for (Path file: stream) 
		    {
		        files.add(file.toAbsolutePath().toString());
		    }
		} catch (Exception x) {
		    // IOException can never be thrown by the iteration.
		    // In this snippet, it can only be thrown by newDirectoryStream.
		    Logger.WriteError("Error accessing to file", x);
		}
		return files;
    }
	
	private static void PrintResultFiles(String[] input_files, String input_text)
			throws FileNotFoundException, IOException {
		if (input_text != null) {
			System.err.println("Result:\n");
			BufferedReader reader = new BufferedReader(new FileReader(input_files[0] + ".tml"));
			try{
				String text = null;
				while ((text = reader.readLine()) != null) {
					System.out.println(text + "\n");
				}
			}finally{
				if(reader!=null) reader.close();
			}
		}
	}
}
