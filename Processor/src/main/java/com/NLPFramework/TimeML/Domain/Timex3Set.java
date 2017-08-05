package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;
import com.NLPFramework.Domain.Word;

public class Timex3Set extends Timex3 implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Timex3Set(Word w) {
		super(w);
		type = TimeType.SET;
		// TODO Auto-generated constructor stub
	}
	public String freq;
	public String quant;
}
