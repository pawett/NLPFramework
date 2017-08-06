package com.NLPFramework.TimeML.Train;

import java.io.File;
import java.io.IOException;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedFileHashtable;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Processor.FeatureExtractorEnglish;
import com.NLPFramework.Processor.NLPProcessor;
import com.NLPFramework.Processor.TemporalInformationProcessingStrategy;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;

public class TrainTMLBase extends TrainBase implements ITrainExecutor {

	public TrainTMLBase(File train_dir, File modelDir, String approach,
			Language lang,
			TemporalInformationProcessingStrategy strategy)
	{
		super(train_dir, modelDir, approach, lang, strategy);

	}
	
	public TrainTMLBase(File train_dir, File modelDir, String approach,
			Language lang, 
			IMachineLearningMethod method)
	{
		super(train_dir, modelDir, approach, lang, method);
	}


	public String getApproachTypeElemName(String filter)
	{
		String elem = getElement();
		TimeMLTrainTypes type = getType();
		if(filter != null)
			return  approach + "_"  + type + "_" + elem + "_" + filter + "_" + lang;
		else
			return  approach + "_"  + type + "_" + elem + "_" + lang;
	}

	protected String train(ISentenceFormatter formatter, IMachineLearningMethod mlMethod, String filter)
	{		
		String model = modelDir + File.separator +  mlMethod.getClass().getSimpleName() + File.separator + "models" + File.separator + getApproachTypeElemName(filter) + "." + mlMethod.getClass().getSimpleName() + "model";
		Logger.WriteDebug("Training model " + model);
		
		if(!new File(model).exists())
		{ 
			String featuresTrainDir = train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features";

			String featuresFilePath = featuresTrainDir + File.separator + "features.obj";

			TokenizedFileHashtable files = FileHelper.getBinaryFiles(featuresFilePath);
			
			/*files.keySet().parallelStream().forEach((fileName) -> {
				TokenizedFile kFileTemp = files.get(fileName);
				
				FeatureExtractorEnglish featureExtractor = new FeatureExtractorEnglish();
				featureExtractor.setFeatures(kFileTemp);
			});
			*/
			File trainFile = new File(featuresTrainDir + File.separator + getApproachTypeElemName(filter) + ".pipes");
			trainFile = files.toFile(formatter, trainFile.getAbsolutePath());

			String output = mlMethod.Train(trainFile.getAbsolutePath(), getApproachTypeElemName(filter) + ".template");
		
			(new File(output)).renameTo(new File(model));
		}
		return model;
	}
	
	protected String train(IFileFormatter formatter, IMachineLearningMethod mlMethod, String filter)
	{		
		String model = modelDir +  File.separator + mlMethod.getClass().getSimpleName() + File.separator + "models" + File.separator + getApproachTypeElemName(filter) + "." + mlMethod.getClass().getSimpleName() + "model";
		Logger.WriteDebug("Training model " + model);
		
		if(!new File(model).exists())
		{ 
			String featuresTrainDir = train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features";

			String featuresFilePath = featuresTrainDir + File.separator + "features.obj";

			TokenizedFileHashtable files = FileHelper.getBinaryFiles(featuresFilePath);

			File trainFile = new File(featuresTrainDir + File.separator + getApproachTypeElemName(filter) + ".pipes");
			trainFile = files.toFile(formatter, trainFile.getAbsolutePath());

			String output = mlMethod.Train(trainFile.getAbsolutePath(), getApproachTypeElemName(filter) + ".template");
		
			(new File(output)).renameTo(new File(model));
		}
		return model;
	}
	

	/*public void NormalizationType_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			File dir = FileHelper.GetFileAndCreateDir(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}

			String model = dir + File.separator + approach + "_timen_" + elem + "_" + lang + "." + strategy.getTimexProcessing().getNormalization() + "model";
			output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String features = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			output = Classification.get_classik(features, lang);
			output = TimexNormalization.getTIMEN(features, output, lang);

			output = strategy.getTimexProcessing().getNormalization().Train(output, approach + "_timen_" + elem + ".template");

			(new File(output)).renameTo(new File(model));

			features = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			output = Classification.get_classik(features, lang);
			output = TimexNormalization.getTIMEN(features, output, lang);

			output = strategy.getTimexProcessing().getNormalization().Test(output, model);


			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));

			key = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String keyfeatures = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			key = Classification.get_classik(keyfeatures, lang);
			key = TimexNormalization.getTIMEN(keyfeatures, key, lang);


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);
			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			score.print("");


		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
		}
	}

	public void categorization(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			if (!elem.matches("e-(t|dct|main|sub)")) {
				throw new Exception("elem must match:e-(t|dct|main|sub). Found: " + elem);
			}

			File dir = FileHelper.GetFileAndCreateDir(train_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			String model = dir + File.separator + approach + "_categ_" + elem + "_" + lang + "." + strategy.getTemporalRelationProcessing().getEvent_timex().getClass().getSimpleName() + "model";
			output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem) + "-annotationKey";

			output = strategy.getTemporalRelationProcessing().getEvent_timex().Train(output, approach + "_categ_" + elem + ".template");

			(new File(output)).renameTo(new File(model));


			output = test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem);

			output = strategy.getTemporalRelationProcessing().getEvent_timex().Test(output, model);


			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			//PipesFile nlpannot = new PipesFile();
            //nlpannot.loadFile(new File(annot));
            //((PipesFile) nlpannot).isWellFormedOptimist();
			 
			key = test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get(elem) + "-annotationKey";


			// TempEvalFiles-2 results
			Logger.Write("Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);

			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			//score.print("attribs");
			//score.print("detail");
			//score.print(printopts);
			score.print("");

		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
		}

	}
*/
	// test normalization...
	/*public void Normalization_tml(String elem) {
		String output = "", key;
		Scorer scorer = new Scorer();
		try {
			File dir = FileHelper.GetFileAndCreateDir(test_dir.getParent() + File.separator + "experiments_tml" + File.separator + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(test_dir, approach, lang);
			}
			output = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String features = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			//output = Classification.get_classik_old(features, lang);
			//output = TimexNormalization.getTIMEN_old(features, output, lang);
			//output = TimexNormalization.get_normalized_values(output, lang);
			Logger.Write(output);
			String annot = dir + File.separator + (new File(output)).getName();
			(new File(output)).renameTo(new File(annot));
			key = TempEvalFiles.merge_extents(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-extents.tab", elem);
			String keyfeatures = TempEvalFiles.merge_attribs(test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-" + elem, test_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + elem + "-attributes.tab", elem);
			//key = Classification.get_classik_old(keyfeatures, lang);
			//key = TimexNormalization.getTIMEN_old(keyfeatures, key, lang);
			//key = TimexNormalization.get_key_normalized_values(key);
			// TempEvalFiles-2 results
			Logger.Write("Testset Results: " + approach);
			//TempEval_scorer.score_entities(extents, TempEvalpath +lang+"/test/entities/"+ elem + "-attributes.tab", lang, elem);
			// AnnotScore results
			Score score = scorer.score_class(annot, key, -1);
			score.print("detail");
		} catch (Exception e) {
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}
*/
	/*public void full_tml() {
		String output = "";
		try 
		{
			File dir_trainmodels = FileHelper.GetFileAndCreateDir(train_dir.getCanonicalPath() + "-models-" + approach + File.separator);
			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			/*if (re_build_dataset.equalsIgnoreCase("true") || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
            FileConverter.tmldir2features(test_dir, approach, lang);
            }*/
/*
			String model = dir_trainmodels + File.separator + approach + "_rec_timex_" + lang + ".CRFmodel";
			// check if already trained
			if (!(new File(model)).exists()) 
			{
    				TrainTimex(dir_trainmodels, model);
				// event
				TrainEvents(dir_trainmodels);
				// links
				TrainLinks(dir_trainmodels);
			}

			// test

			File dir_test_annotation = FileHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-" + approach + File.separator);
			File dir_test_te3input = FileHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-input-" + approach + File.separator);

			File[] tmlfiles = test_dir.listFiles(FileUtils.onlyFilesFilter);
			for (int i = 0; i < tmlfiles.length; i++) 
			{
				XMLFile nlpfile =  FileHelper.GetNLPFile(tmlfiles[i].getAbsolutePath());

				String te3input = TML_file_utils.TML2TE3(nlpfile.getFile().getCanonicalPath());
				String basefile = dir_test_te3input + File.separator + new File(te3input).getName();
				(new File(te3input)).renameTo(new File(basefile));
				nlpfile= FilesHelper.GetNLPFile(basefile);
				nlpfile.setLanguage(lang);
				Annotator tip = new Annotator(nlpfile, "te3input", approach, lang, null, null, dir_trainmodels.getCanonicalPath(), new TemporalInformationProcessingStrategy());
				output = tip.Annotate();
				(new File(output)).renameTo(new File(dir_test_annotation + File.separator + tmlfiles[i].getName()));
			}

		} catch (Exception e) 
		{
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}

*/
	private void TrainLinks(File dir_trainmodels) throws IOException {
		String output;
		String model;
		TemporalInformationProcessingStrategy localStrategyCRF = new TemporalInformationProcessingStrategy(new CRF());
		TemporalInformationProcessingStrategy localStrategySVM = new TemporalInformationProcessingStrategy(new SVM());
		model = dir_trainmodels + File.separator + approach + "_categ_e-t_" + lang + ".SVMmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-t") + "-annotationKey";
		output = localStrategySVM.getTemporalRelationProcessing().getEvent_timex().Train(output, approach + "_categ_e-t.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-dct_" + lang + ".SVMmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-dct") + "-annotationKey";
		output = localStrategySVM.getTemporalRelationProcessing().getEvent_DCT().Train(output, approach + "_categ_e-dct.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-main_" + lang + ".CRFmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-main") + "-annotationKey";
		output = localStrategyCRF.getTemporalRelationProcessing().getMain_events().Train(output, approach + "_categ_e-main.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_categ_e-sub_" + lang + ".CRFmodel";
		output = train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + category_files.get("e-sub") + "-annotationKey";
		output = localStrategyCRF.getTemporalRelationProcessing().getSubordinate_events().Train(output, approach + "_categ_e-sub.template");
		(new File(output)).renameTo(new File(model));
	}

	@Override
	public String getElement() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TimeMLTrainTypes getType() {
		// TODO Auto-generated method stub
		return null;
	}

/*	private void TrainEvents(File dir_trainmodels) throws IOException {
		String output;
		String model;
		TemporalInformationProcessingStrategy localStrategyCRF = new TemporalInformationProcessingStrategy(new CRF());
		TemporalInformationProcessingStrategy localStrategySVM = new TemporalInformationProcessingStrategy(new SVM());
		model = dir_trainmodels + File.separator + approach + "_rec_event_" + lang + ".CRFmodel";
		System.out.println("model: " + model);
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-extents.tab", "event");
		output = localStrategyCRF.getEventProcessing().getRecognition().Train(output, approach + "_rec_event.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_class_event_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-extents.tab", "event");
		output = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-event", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "event-attributes.tab", "event");
		//output = Classification.get_classik_old(output, lang);
		output = localStrategySVM.getEventProcessing().getClassification().Train(output, approach + "_class_event.template");
		(new File(output)).renameTo(new File(model));
	}

	private void TrainTimex(File dir_trainmodels, String model) throws IOException {
		String output;
		TemporalInformationProcessingStrategy localStrategyCRF = new TemporalInformationProcessingStrategy(new CRF());
		TemporalInformationProcessingStrategy localStrategySVM = new TemporalInformationProcessingStrategy(new SVM());
		//timex
		Logger.Write("model: " + model);
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		output = localStrategyCRF.getTimexProcessing().getRecognition().Train(output, approach + "_rec_timex.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_class_timex_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		output = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-timex", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-attributes.tab", "timex");
		output = Classification.get_classik(output, lang);
		output = localStrategySVM.getTimexProcessing().getClassification().Train(output, approach + "_class_timex.template");
		(new File(output)).renameTo(new File(model));

		model = dir_trainmodels + File.separator + approach + "_timen_timex_" + lang + ".SVMmodel";
		output = TempEvalFiles.merge_extents(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-extents.tab", "timex");
		String features = TempEvalFiles.merge_attribs(train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features-annotationKey-timex", train_dir.getCanonicalPath() + "_" + approach + "_features" + File.separator + "timex-attributes.tab", "timex");
		output = Classification.get_classik(features, lang);
		output = TimexNormalization.getTIMEN(features, output, lang);
		output = localStrategySVM.getTimexProcessing().getNormalization().Train(output, approach + "_timen_timex.template");
		(new File(output)).renameTo(new File(model));
	}
*/
	
	/*
	public void idcat_tml(String strategy) {
		String output = "";
		try {
			File dir_trainmodels = FileHelper.GetFileAndCreateDir(train_dir.getCanonicalPath() + "-models-" + approach + File.separator);
			File dir_test_annotation = FileHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-" + approach + "-links-"+ strategy + File.separator);
			File dir_test_te3input = FileHelper.GetFileAndCreateDir(test_dir.getCanonicalPath() + "-input4links-" + approach + File.separator);

			// Check for features files (train/test)
			if (rebuild_dataset || !(new File(train_dir.getParent() + File.separator + train_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) 
			{
				FileConverter.tmldir2features(train_dir, approach, lang);
			}
			/*if (re_build_dataset.equalsIgnoreCase("true") || !(new File(test_dir.getParent() + File.separator + test_dir.getName() + "_" + approach + "_features" + File.separator + "base-segmentation.TempEval2-features")).exists()) {
            FileConverter.tmldir2features(test_dir, approach, lang);
            }*/

/*			String model = dir_trainmodels + File.separator + approach + "_categ_e-t_" + lang + ".SVMmodel";
			// check if already trained
			if (!(new File(model)).exists() && !strategy.equalsIgnoreCase("super-baseline")) 
			{
				TrainLinks(dir_trainmodels);
			}

			// test
			File[] tmlfiles = test_dir.listFiles(FileUtils.onlyFilesFilter);
			for (int i = 0; i < tmlfiles.length; i++) 
			{
				XMLFile nlpfile = FilesHelper.GetNLPFile(tmlfiles[i].getAbsolutePath());

				String onlyEntitiesInput = TML_file_utils.TML2onlyEntities(nlpfile.getFile().getCanonicalPath());
				String basefile = dir_test_te3input + File.separator + new File(onlyEntitiesInput).getName();
				(new File(onlyEntitiesInput)).renameTo(new File(basefile));
				nlpfile= FilesHelper.GetNLPFile(basefile);
				nlpfile.setLanguage(lang);
				Annotator tip = new Annotator(nlpfile,null, approach, lang, null,null, dir_trainmodels.getCanonicalPath(),null);
				output = tip.annotate_links(strategy);
				(new File(output)).renameTo(new File(dir_test_annotation + File.separator + tmlfiles[i].getName()));
			}

		} catch (Exception e) 
		{
			Logger.WriteError("Errors found (Experimenter):\n\t" + e.toString() + "\n", e);
			System.exit(1);
		}
	}
*/
}
