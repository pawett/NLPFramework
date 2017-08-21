package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.PipesHelper;

public class SennaFormatter  implements IWordFormatter {

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
			 //if(sr.verb != null)
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
		if(values == null)
			return false;
		String[] valuesArray = values.split("\t");
		//word.word = valuesArray[0];
		//word.pos = valuesArray[2];
		if(!FileHelper.formatText(valuesArray[0]).trim().equalsIgnoreCase(FileHelper.formatText(word.word).trim()))
			return false;
		word.pos = valuesArray[1].trim();
		String[] syntValues = valuesArray[2].trim().split("-");
		if(syntValues.length >= 2)
		{
			word.syntbio = syntValues[0];
			word.synt = syntValues[1];
		}else
			word.syntbio = valuesArray[2].trim();
		
		if(!valuesArray[3].trim().equals("O"))
				word.ner = valuesArray[3].trim().split("-")[1];
		
		if(!valuesArray[4].trim().equals("-") && word.pos.startsWith("V"))
			word.isVerb = true;
		for(int i = 5; i <= valuesArray.length -2; i++)
			word.setRoleFromText(valuesArray[i].trim());
		
		word.syntacticTree = valuesArray[valuesArray.length -1];
		return true;
//ID FORM LEMMA TAG SHORT_TAG MSD NEC SENSE SYNTAX DEPHEAD DEPREL COREF SRL		
	}



	@Override
	public boolean isSentenceEnd(String text) {
		String [] values = text.split("\t");
		if(values[0].equals("."))return true;
		return false;
	}


	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "senna";
	}


}


