package com.NLPFramework.Domain;

import java.util.ArrayList;

import com.NLPFramework.TimeML.Domain.Event;
import com.NLPFramework.TimeML.Domain.Timex3;


public class JournalistInfo  extends Annotation
{
	public Event what;
	public ArrayList<Entity> actors = new ArrayList<>();
	public ArrayList<Entity> patients = new ArrayList<>();
	public Timex3 when;
}
