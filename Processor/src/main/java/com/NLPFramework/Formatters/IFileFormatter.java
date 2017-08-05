package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.TimeML.Domain.TimeMLFile;

public interface IFileFormatter 
{
	String toString(TimeMLFile file);
	String getExtension();
	void setValues(String values, TimeMLFile file);
}
