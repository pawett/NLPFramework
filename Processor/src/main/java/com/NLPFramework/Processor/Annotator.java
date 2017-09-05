package com.NLPFramework.Processor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Formatters.TempEval2FeaturesAnnotated;
import com.NLPFramework.Formatters.TempEval2FeaturesFormatter;
import com.NLPFramework.Formatters.TempEvalClassikAnnotatedFormatter;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;


public class Annotator {
	
	private static final String FilesType = null;
	private File nlpfile;
	
	private String approach;
	private String entities;

	private String models_path;
	private Language lang;
	private Timex3 dct;
	private TemporalInformationProcessingStrategy method;
	
	public Annotator(File nlpfile, String approach, String models_path, TemporalInformationProcessingStrategy method)
	{
		this.nlpfile = nlpfile;	
		this.approach = approach;
		this.method = method;
		
		
		if(entities == null)
		{
			entities ="timex;event;tlink";
		}
		
	

		if (models_path == null) 
		{
			this.models_path = "";
		} else 
		{
			this.models_path += File.separator;
		}
		
	}
	
	public  String get_last_text_blanks(String file) {
		String last_text_blanks = "\n";
		try {
			File f = new File(file);
			RandomAccessFile raf = new RandomAccessFile(f, "r");
			//String filecontents = null;
			//filecontents = FileUtils.readFileAsString(file, "UTF-8");
			/*if(filecontents.endsWith("\n")){
            filecontents=filecontents.substring(0, filecontents.length()-1);
            }*/
			/*last_text_blanks = filecontents.replaceAll(".+(\\s+)$", ".$1.");
            if(filecontents.equals(last_text_blanks)){
            last_text_blanks="\n";
            }*/
			for (long i = f.length() - 2; i > 0; i--) {
				raf.seek(i);
				char c = (char) raf.readByte();
				if (c == '\n' || c == '\r' || c == '\t' || c == ' ') {
					last_text_blanks = c + last_text_blanks;
				} else {
					break;
				}
			}
			if(raf!=null){
				raf.close();
			}
		} catch (Exception e) {
			Logger.WriteError("Errors found (TML_file_utils):\n\t" + e.toString() + "\n", e);
			return null;
		}
		return last_text_blanks;
	}

	/**
	 * Annotate a plain or te3input file, specifying: the file, intput format, approach, language, entities to annotate,
	 * dctvalue, and the path to the models used for annotating.
	 *
	 * @param nlpfile
	 * @param inputf
	 * @param approach
	 * @param lang
	 * @param entities
	 * @param dctvalue
	 * @param models_path
	 * @return
	 */
	public String Annotate() {
		String ret = null;
		try {

			//File dir = FilesHelper.GetFileAndCreateDir(nlpfile.getFile().getCanonicalPath() + "_" + approach + "_features" + File.separator);
			NLPProcessor processor = new NLPProcessor(nlpfile.getAbsolutePath(), method);
			
			//TokenizedFile features  = PlainTokenFeatures.getFeatures(nlpfile, lang, 1, false, approach);
			TokenizedFile features  = processor.setFeatures();
					//
						
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
			
			//features.setDCTFromDate(sdf.format(new Date()));
			
			String timex_merged = null;
			if (entities.contains("timex")) 
			{
				processor.processTimex();
				//RecognizeTIMEX3(features);
			}

			String event_merged = null;
			if (entities.contains("event")) {
				//File featuresFile = features.toFile();
				processor.recogniceEvents();

				processor.classifyEvents();//RecognizeEvents(features);
			}
			// Omit signals for the moment: wait for longer and better corpus
			/*String signal;
            if(lang.equalsIgnoreCase("EN")){
            if (System.getProperty("DEBUG") != null && System.getProperty("DEBUG").equalsIgnoreCase("true")) {
            System.err.print("Recognizing SIGNALS");
            }
            signal = CRF.test(features, model+"_rec_signal_"+nlpfile.getLanguage().toUpperCase()+".CRFmodel");
            nlpfile_temp = new PipesFile();
            nlpfile_temp.loadFile(new File(signal));
            ((PipesFile) nlpfile_temp).isWellFormedOptimist();
            signal=PipesFile.IOB2check(nlpfile_temp);
            all_merged=PipesFile.merge_pipes(all_merged,signal);
            }*/

			//String all_merged = PipesFile.merge_pipes(timex_merged, event_merged);
			//all_merged = BaseTokenFeatures.putids(all_merged);
			processor.setIds();
			//features.setIds();
			
			
			if (entities.contains("tlink")) {
				//tml = RecognizeTLINKS(features);
				processor.RecognizeTLINKS();
			}
			
			TimeMLFile timeMLFile = (TimeMLFile)(processor.getFile());
			ret = processor.getTimeMLFile().toTML(timeMLFile);
			
			


		} catch (Exception e) {
			Logger.WriteError("\nErrors found (TIP):\n\t" + e.toString() + "\n", e);
			return null;
		}
		return ret;
	}

	private TimeML RecognizeTLINKS(TokenizedFile features)
	{
		Logger.WriteDebug("Recognizing TLINKs");
		TimeMLFile timeFeatures = new TimeMLFile(features.getLanguage(), features.getName());

		TimeML tml = new TimeML(timeFeatures);

		if (!entities.contains("tlinkspecial")) {
		/*	File file = tml.toFileEventDCT(null);
			String model =  approach + "_categ_" + "e-dct" + "_" + lang + "." + method.getTemporalRelationProcessing().getEvent_timex().getClass().getSimpleName() + "model";
			String eDCTLinksResult = method.getTemporalRelationProcessing().getEvent_DCT().Test(file.getAbsolutePath(), model);
			
			tml.updateEventDCTFromFileReader(eDCTLinksResult);
			
			file = tml.toFileEventTimex(null);
			model = approach + "_categ_" + "e-t" + "_" + lang + "." + method.getTemporalRelationProcessing().getEvent_timex().getClass().getSimpleName() + "model";
			String eTimexLinksResult = method.getTemporalRelationProcessing().getEvent_DCT().Test(file.getAbsolutePath(), model);
			
			tml.updateEventTimexFromFileReader(eTimexLinksResult);
			
			file = tml.toFileMainEventEvent(null);
			model = approach + "_categ_" + "e-main" + "_" + lang + "." + method.getTemporalRelationProcessing().getEvent_timex().getClass().getSimpleName() + "model";
			String eMainEventLinksResult = method.getTemporalRelationProcessing().getEvent_DCT().Test(file.getAbsolutePath(), model);
			
			tml.updateMainEventEventFromFileReader(eMainEventLinksResult);
						
			file = tml.toFileSubEventEvent(null);
			model = approach + "_categ_" + "e-sub" + "_" + lang + "." + method.getTemporalRelationProcessing().getEvent_timex().getClass().getSimpleName() + "model";
			String eSubEventLinksResult = method.getTemporalRelationProcessing().getEvent_DCT().Test(file.getAbsolutePath(), model);
			
			tml.updateSubEventEventFromFileReader(eSubEventLinksResult);
			*/
		}
		return tml;
	}

	private void RecognizeEvents(TokenizedFile features) 
	{
		Logger.WriteDebug("Recognizing EVENTs");
		//features.setType(FilesType.TempEval2_features);
		TimeMLFile timeFeatures = new TimeMLFile(features.getLanguage(), features.getName());

		method.getEventProcessing().getRecognition().Test(features, models_path, approach, "rec_event", timeFeatures.getLanguage(), new TempEval2FeaturesFormatter(),  new TempEval2FeaturesAnnotated());
		
		Logger.WriteDebug("Classifying EVENTs");

		Classification.setClassikEvents(features);
		String event_class;
		//features.setType(FilesType.TempEval_classik_event_features);
		method.getEventProcessing().getClassification().Test(features, models_path, approach, "class_event", timeFeatures.getLanguage(), new TempEval2FeaturesAnnotated(), new TempEvalClassikAnnotatedFormatter());

	}

	
	public static String categorize_baseline(String features, String mfcategory){
		String output=features+"-annotatedWith-Baseline";
		try{
			BufferedReader reader = new BufferedReader(new FileReader(features));
			BufferedWriter textwriter = new BufferedWriter(new FileWriter(new File(output)));
			try {
				String line="";
				while ((line = reader.readLine()) != null) {
					if (line.length() > 0) {
						textwriter.write(line+"|"+mfcategory+"\n");
					}
				}
			} finally {
				if (reader != null) {
					reader.close();
				}
				if (textwriter != null) {
					textwriter.close();
				}
			}
		} catch (Exception e) {
			Logger.WriteError("\nErrors found (TIP):\n\t" + e.toString() + "\n", e);
			return null;
		}
		return output;
	}

}
