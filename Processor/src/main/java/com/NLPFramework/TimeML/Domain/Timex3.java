package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;

import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Word;


public class Timex3 extends Annotation  implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public TimeType type = TimeType.DATE;
	public String value;
	public String mod;
	public boolean temporalFunction;
	public String anchorTimeID;
	public String valueFromFunction;
	public TimeFunctionInDocument functionInDocument = TimeFunctionInDocument.NONE;
	public Word word;

	public Timex3()
	{
		
	}
	public Timex3(Word w)
	{
		if(w != null)
		{
			//id = w.id;
			//setType(w);
			//value = w.norm_type2_value;
			word = w;
		}
	}

	private void setType(Word w) 
	{
		if(w.norm_type2.equals("DATE") || w.norm_type2.equals("TIME") ||
				w.norm_type2.equals("DURATION") ||
				w.norm_type2.equals("SET"))
			type = TimeType.valueOf(w.norm_type2); 	
	}
}
