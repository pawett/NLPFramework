package com.NLPFramework.Domain;

import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.Timex3;


public class JournalistInfo  extends Annotation
{
	public Event what;
	public Entity actor;
	public Entity patient;
	public Timex3 when;
}
