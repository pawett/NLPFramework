package com.NLPFramework.TimeML.Train;

public interface ITrainExecutor{
	public void execute() throws Exception;
	public String getElement();
	public TimeMLTrainTypes getType();
}
