package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;
import com.NLPFramework.Domain.Word;

public class Timex3Duration extends Timex3 implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Timex3Duration(Word w) {
		super(w);
		type = TimeType.DURATION;
		// TODO Auto-generated constructor stub
	}
	public Timex3 beginPoint;
	public Timex3 endPoint;
}
