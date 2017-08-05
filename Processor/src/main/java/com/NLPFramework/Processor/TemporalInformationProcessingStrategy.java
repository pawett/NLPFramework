package com.NLPFramework.Processor;

import com.NLPFramework.Domain.Language;
import com.NLPFramework.externalTools.CRF;
import com.NLPFramework.externalTools.IMachineLearningMethod;
import com.NLPFramework.externalTools.SVM;

public class TemporalInformationProcessingStrategy {
	
	private TimexProcessing timexProcessing;
	private EventProcessing eventProcessing;
	private TemporalRelationProcessing temporalRelationProcessing;
	
	public TimexProcessing getTimexProcessing()
	{
		return timexProcessing;
	}
	
	public void setTimexProcessing(TimexProcessing timexP)
	{
		timexProcessing = timexP;
	}
	

	public EventProcessing getEventProcessing()
	{
		return eventProcessing;
	}
	
	public void setEventProcessing(EventProcessing eventP)
	{
		eventProcessing = eventP;
	}
	

	public TemporalRelationProcessing getTemporalRelationProcessing()
	{
		return temporalRelationProcessing;
	}
	
	public void setTemporalRelationProcessing(TemporalRelationProcessing temporalRelationP)
	{
		temporalRelationProcessing = temporalRelationP;
	}
	
	public TemporalInformationProcessingStrategy(Language lang)
	{
		switch(lang)
		{
		case EN:
			timexProcessing = new TimexProcessing();
			timexProcessing.setRecognition(new CRF());
			timexProcessing.setClassification(new SVM());
			timexProcessing.setNormalization(new SVM());
			
			eventProcessing = new EventProcessing();
			eventProcessing.setRecognition(new CRF());
			eventProcessing.setClassification(new SVM());
			
			temporalRelationProcessing = new TemporalRelationProcessing();
			temporalRelationProcessing.setEvent_timex(new CRF());
			temporalRelationProcessing.setEvent_DCT(new CRF());
			temporalRelationProcessing.setMain_events(new CRF());
			temporalRelationProcessing.setSubordinate_events(new CRF());
			
			break;
		case ES:
			timexProcessing = new TimexProcessing();
			timexProcessing.setRecognition(new CRF());
			timexProcessing.setClassification(new SVM());
			timexProcessing.setNormalization(new SVM());
			
			eventProcessing = new EventProcessing();
			eventProcessing.setRecognition(new CRF());
			eventProcessing.setClassification(new SVM());
			
			temporalRelationProcessing = new TemporalRelationProcessing();
			temporalRelationProcessing.setEvent_timex(new SVM());
			temporalRelationProcessing.setEvent_DCT(new SVM());
			temporalRelationProcessing.setMain_events(new CRF());
			temporalRelationProcessing.setSubordinate_events(new CRF());

			break;
		}
	}
	
	
	public TemporalInformationProcessingStrategy()
	{
		//Default strategy
		timexProcessing = new TimexProcessing();
		timexProcessing.setRecognition(new CRF());
		timexProcessing.setClassification(new SVM());
		timexProcessing.setNormalization(new SVM());
		
		eventProcessing = new EventProcessing();
		eventProcessing.setRecognition(new CRF());
		eventProcessing.setClassification(new SVM());
		
		temporalRelationProcessing = new TemporalRelationProcessing();
		temporalRelationProcessing.setEvent_timex(new CRF());
		temporalRelationProcessing.setEvent_DCT(new CRF());
		temporalRelationProcessing.setMain_events(new CRF());
		temporalRelationProcessing.setSubordinate_events(new CRF());
	}
	
	public TemporalInformationProcessingStrategy(IMachineLearningMethod method)
	{
		//Default strategy
		timexProcessing = new TimexProcessing();
		timexProcessing.setRecognition(method);
		timexProcessing.setClassification(method);
		timexProcessing.setNormalization(method);
		
		eventProcessing = new EventProcessing();
		eventProcessing.setRecognition(method);
		eventProcessing.setClassification(method);
		
		temporalRelationProcessing = new TemporalRelationProcessing();
		temporalRelationProcessing.setEvent_timex(method);
		temporalRelationProcessing.setEvent_DCT(method);
		temporalRelationProcessing.setMain_events(method);
		temporalRelationProcessing.setSubordinate_events(method);
	}

}
