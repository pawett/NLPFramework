package com.NLPFramework.TimeML.Domain;

import java.io.Serializable;
import com.NLPFramework.Domain.Word;


public class Timex3Time extends Timex3 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Timex3Time(Word w) {
		super(w);
		type = TimeType.TIME;
		// TODO Auto-generated constructor stub
	}

}
