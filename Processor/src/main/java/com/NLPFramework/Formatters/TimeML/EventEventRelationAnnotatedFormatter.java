package com.NLPFramework.Formatters.TimeML;

import java.util.LinkedList;

import com.NLPFramework.Formatters.IFileFormatter;
import com.NLPFramework.Helpers.PipesHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.MakeInstance;
import com.NLPFramework.TimeML.Domain.TimeLink;
import com.NLPFramework.TimeML.Domain.TimeLinkRelationType;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.TimeML.Domain.Timex3;

public class EventEventRelationAnnotatedFormatter extends EventEventRelationFormatter implements IFileFormatter 
{
	
	public String toString(TimeMLFile file)
	{
		StringBuilder sb = new StringBuilder();

		for(Object tlObject : file.annotations.get(TimeLink.class))
		{
			String annotation = annotateTimeLink(file, tlObject);
			if(annotation != null)
			{
				sb.append(annotation);
				sb.append(System.lineSeparator());
				sb.append(System.lineSeparator());
			}
		}
		
		return sb.toString();				
	}


	public String annotateTimeLink(TimeMLFile file, Object tlObject)
	{
		TimeLink tl = (TimeLink)tlObject;
		StringBuilder sb = new StringBuilder();
		String annotation = super.annotateTimeLink(file, tlObject);
		TimeLinkRelationType type = TimeMLHelper.getTimeLinkRelationTypeSimplified(tl.type, false);
		if(annotation != null)// && !type.equals(TimeLinkRelationType.INCLUDES))
		{
			sb.append(annotation);
			sb.append(PipesHelper.AppendPipes(type));
			return sb.toString();
		}
		return null;
	}
	
	


	public String getExtension() {
		// TODO Auto-generated method stub
		return "e-t_relation_annotated";
	}

	@Override
	public void setValues(String values, TimeMLFile file) 
	{
		String[] val = values.split("\\|");
		String lId = val[1];
		TimeLinkRelationType type = TimeLinkRelationType.valueOf(val[17]);
		if(file.annotations.get(TimeLink.class) != null)
		{
			for(Object annotation : file.annotations.get(TimeLink.class))
			{
				TimeLink tl = (TimeLink) annotation;
				if(tl.id.equalsIgnoreCase(lId))
					tl.type = type;
			}

		}	
	}

}
