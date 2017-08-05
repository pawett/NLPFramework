package com.NLPFramework.externalTools;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Formatters.ISentenceFormatter;
import com.NLPFramework.Formatters.IWordFormatter;

public interface IMachineLearningMethod 
{
	void Test(TokenizedFile featuresfile, String models_path, String approach, String type, Language lang, IWordFormatter fromFormatter, IWordFormatter toFormatter);
	void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, ISentenceFormatter fromFormatter, ISentenceFormatter toFormatter);
	void Test(TokenizedFile tokenizedFile, String models_path, String approach, String type, Language lang, IFileFormatter fromFormatter, IFileFormatter toFormatter);
	String Test(String featuresfile, String model);
	String Train(String featuresfile, String templatefile);
}
