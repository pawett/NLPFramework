package com.NLPFramework.Formatters.TimeML;

import com.NLPFramework.TimeML.Domain.TimeLink;

public interface ITLinkFormatter {

	public String toString(TimeLink link);
	public void setValues(TimeLink link, String values);
}
