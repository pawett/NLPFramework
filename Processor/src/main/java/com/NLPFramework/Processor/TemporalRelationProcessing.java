package com.NLPFramework.Processor;

import com.NLPFramework.externalTools.IMachineLearningMethod;

public class TemporalRelationProcessing {

	private IMachineLearningMethod event_timex;
	private IMachineLearningMethod event_DCT;
	private IMachineLearningMethod main_events;
	private IMachineLearningMethod subordinate_events;
	public IMachineLearningMethod getEvent_timex() {
		return event_timex;
	}
	public void setEvent_timex(IMachineLearningMethod event_timex) {
		this.event_timex = event_timex;
	}
	public IMachineLearningMethod getEvent_DCT() {
		return event_DCT;
	}
	public void setEvent_DCT(IMachineLearningMethod event_DCT) {
		this.event_DCT = event_DCT;
	}
	public IMachineLearningMethod getMain_events() {
		return main_events;
	}
	public void setMain_events(IMachineLearningMethod main_events) {
		this.main_events = main_events;
	}
	public IMachineLearningMethod getSubordinate_events() {
		return subordinate_events;
	}
	public void setSubordinate_events(IMachineLearningMethod subordinate_events) {
		this.subordinate_events = subordinate_events;
	}
}
