package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class StanfordNLPFormatter  implements IWordFormatter {

	public String toString(Word w)
	{
		StringBuilder sb = new StringBuilder();
		 sb.append(w.word);
		 sb.append(PipesHelper.AppendPipes(w.pos));
		 sb.append(PipesHelper.AppendPipes(w.lemma));
		 SemanticRole sr = w.getSemanticRole();
		 if(sr != null)
		 {
			 sb.append(PipesHelper.AppendPipes(w.getSemanticRole().IOB));
			// if(sr.verb != null)
			//	 sb.append(PipesHelper.AppendPipes(w.getSemanticRole().verb.lemma));
			 if(sr.argument != null)
				 sb.append(PipesHelper.AppendPipes(w.getSemanticRole().argument.toString()));
		 }
		 sb.append(PipesHelper.AppendPipes(w.synt));
		 return sb.toString();
	}
	
	
	//word pos syntacticBIO X verb verb1SemanticRole verb2SemanticRole ... SyntacticHierarchy
	@Override
	public boolean setValues(Word word, String values) 
	{
		String[] valuesArray = values.split("\\|");
		String roleInfo = valuesArray[3];
		if(!roleInfo.equals("O"))
		{
			word.time_IOB2 = valuesArray[3];
			word.element_type = "timex";
			word.element_type_class = valuesArray[4];
			word.norm_type2 = valuesArray[4];
			word.norm_type2_value = valuesArray[5];
			//String[] roleTemp = valuesArray[3].split("-");
			//word.semanticRoleIOB = roleTemp[0];
			//word.semanticRole = roleTemp[1];
		}
		
	
		return true;
//ID FORM LEMMA TAG SHORT_TAG MSD NEC SENSE SYNTAX DEPHEAD DEPREL COREF SRL		
	}

	@Override
	public boolean isSentenceEnd(String text) {
		String [] values = text.split("\\|");
		if(values[2].equals("."))return true;
		return false;
	}


	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "senna";
	}
}


