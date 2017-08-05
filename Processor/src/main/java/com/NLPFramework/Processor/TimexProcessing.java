package com.NLPFramework.Processor;

import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TimexProcessing {
	
	private IMachineLearningMethod recognition;
	private IMachineLearningMethod classification;
	private IMachineLearningMethod normalization;
	public IMachineLearningMethod getRecognition() {
		return recognition;
	}
	public void setRecognition(IMachineLearningMethod recognition) {
		this.recognition = recognition;
	}
	public IMachineLearningMethod getNormalization() {
		return normalization;
	}
	public void setNormalization(IMachineLearningMethod normalization) {
		this.normalization = normalization;
	}
	public IMachineLearningMethod getClassification() {
		return classification;
	}
	public void setClassification(IMachineLearningMethod classification) {
		this.classification = classification;
	}

}
