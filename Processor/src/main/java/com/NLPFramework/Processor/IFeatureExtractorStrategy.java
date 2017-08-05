package com.NLPFramework.Processor;

import com.NLPFramework.Domain.TokenizedFile;

public interface IFeatureExtractorStrategy {

	TokenizedFile setFeatures(TokenizedFile file);
	
}
