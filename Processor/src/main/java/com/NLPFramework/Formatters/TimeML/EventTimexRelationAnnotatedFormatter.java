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

public class EventTimexRelationAnnotatedFormatter extends EventTimexRelationFormatter implements IFileFormatter 
{
	
	/* 0 file| 1 lid | 2 eid | 3 tid |    
	  4 e_class| 5 e_pos| 6 e_token| 7 e_tense | 8 e_tense-aspect|
	   9 e_govPP| 10 e_govTMPSub| 11 t_type| 12 t_ref| 13 t_govPP|
	    14 t_govTMPSub|15 synt_relation
	 */
	//exampletext2.txt|l3|ei2|t2|
	//OCCURRENCE|VERB|buy|FUTURE|FUTURE|FUTURE-NONE|-|-|DATE|reference|-|-|samephra
	public String toString(TimeMLFile file)
	{
		StringBuilder sb = new StringBuilder();
		
		LinkedList<Object> timexes = new LinkedList<>();
		timexes.addAll(file.annotations.get(TimeLink.class));
		timexes.addAll(file.annotations.get(TimeLink.class));
		//
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
		boolean relationIsReverse = false;
		if(tl.relatedToEventInstance != null)
			relationIsReverse = true;
		TimeLinkRelationType type = TimeMLHelper.getTimeLinkRelationTypeSimplified(tl.type, relationIsReverse);
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
