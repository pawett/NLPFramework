package com.NLPFramework.Formatters;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEvalClassikFormatter implements IWordFormatter {

	@Override
	public String toString(Word w) 
	{
		StringBuilder sb = new StringBuilder();
		
		if(w.isBeginElement())
		 {		 
			 sb.append(w.file);
			 sb.append(PipesHelper.AppendPipes(w.sentenceNumber));
			 sb.append(PipesHelper.AppendPipes(w.sentencePosition));
			 sb.append(PipesHelper.AppendPipes(w.word));
			 sb.append(PipesHelper.AppendPipes(w.pos));
			 sb.append(PipesHelper.AppendPipes(w.lemma));
			 sb.append(PipesHelper.AppendPipes(w.rolesconf));
			 SemanticRole sr = w.getSemanticRole();
			 if(sr != null && sr.argument != null){
				 Logger.WriteDebug(w.toString());
				sb.append(PipesHelper.AppendPipes(w.getSemanticRole().argument.toString()));
			 }else
				 sb.append(PipesHelper.AppendPipes("-"));
			 if(w.depverb != null)
			 {
				 sb.append(PipesHelper.AppendPipes(w.depverb.word));
				 sb.append(PipesHelper.AppendPipes(w.depverb.tense));
				 sb.append(PipesHelper.AppendPipes(w.depverb.polarity));
			 }else{
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
			 }
			 sb.append(PipesHelper.AppendPipes(w.mainphraseIOB));
			 sb.append(PipesHelper.AppendPipes(w.preposition));
			 sb.append(PipesHelper.AppendPipes(w.wn));
			 sb.append(PipesHelper.AppendPipes(getGovExtra1(w)));
			 sb.append(PipesHelper.AppendPipes(getGovExtra2(w)));
			 sb.append(PipesHelper.AppendPipes(getGovExtra3(w)));
			 //sb.append(AppendPipes("x4"+extra4));
			// sb.append(AppendPipes("x5"+extra5));
			 sb.append(PipesHelper.AppendPipes(w.extra6));
			 if(w.element_type.matches("event"))
				 sb.append(PipesHelper.AppendPipes(w.event_IOB2));
			 else
				 sb.append(PipesHelper.AppendPipes(w.time_IOB2));
		 }
		 return sb.toString();
	}
	private String extra3Rec = null;
	
	public String getGovExtra3(Word w)
	 {
		 extra3Rec = w.extra3;
		 if(w.govWord != null) 
		 {
			 extra3Rec = "";
			 extra3Rec += printExtra3Recursive(w.govWord);
		 }else{
			 extra3Rec += "|" + w.extra4 + "|" + w.extra5; 	
		 }
		
		 return extra3Rec;
	 }
	 
	 public String printExtra3Recursive(Word w)
	 {
		 extra3Rec = w.extra3 + "|" +
		 		   w.extra4 + "|" +
		 		   w.extra5 ; 
		 if(w.govWord != null) 
		 {
			 extra3Rec+= printShit(w.govWord);
		 }
		 return  extra3Rec;  
	 
	 }
	 
	 public String printShit(Word w)
	 {
		 extra3Rec = w.extra3 + "|" +
		 		   w.extra4 + "|" +
		 		   w.extra5 ; 
		 if(w.govWord != null) 
		 {
			 extra3Rec+= printShit(w.govWord);
		 }
		 return  extra3Rec;  
	 
	 }
	 
	
	 private String wordRec = null;
	 public String getGovWord(Word w)
	 {
		 wordRec = w.word;
		 if(w.govWord != null) 
		 {
			 w.wordRec += "_"+ getGovWord(w.govWord);
		 }
		 return wordRec;
	 }
	 private String posRec = null;
	 public String getGovPos(Word w)
	 {
		 posRec = w.pos;
		 if(w.govWord != null) 
		 {
			 w.posRec += "_"+getGovPos(w.govWord);
		 }
		 return posRec;
	 }
	 
	 private String lemmaRec = null;
	 public String getGovLemma(Word w)
	 {
		 lemmaRec = w.lemma;
		 if(w.govWord != null) 
		 {
			 lemmaRec += "_"+ getGovLemma(w.govWord);
		 }
		 return lemmaRec;
	 }
	 
	 private String extra1Rec = null;
	 public String getGovExtra1(Word w)
	 {
		 extra1Rec = w.syntacticTree;
		 if(w.govWord != null) 
		 {
			 extra1Rec += "_"+ getGovExtra1(w.govWord);
		 }
		 return extra1Rec;
	 }
	
	 private String extra2Rec = null;
	 public String getGovExtra2(Word w)
	 {
		 extra2Rec = w.extra2;
		 if(w.govWord != null) 
		 {
			 extra2Rec += "_" + getGovExtra2(w.govWord);
		 }
		 return extra2Rec;
	 }

	@Override
	public boolean setValues(Word w, String values) 
	{
		String[] pipeArray = values.split("\\|");
		/*w.file = pipeArray[0];
		w.sent_num = pipeArray[1];
		w.tok_num= pipeArray[2];
		w.word = pipeArray[3];
		w.pos = pipeArray[4];
		w.lemma = pipeArray[5];
		w.rolesconf= pipeArray[6];
		w.getSemanticRole().argument = PropBankArgument.valueOf(pipeArray[7]);
		w.depverb= pipeArray[8];
		w.tense= pipeArray[9];
		w.polarity= pipeArray[10];
		w.mainphraseIOB= pipeArray[11];
		w.preposition= pipeArray[12];
		w.wn= pipeArray[13];*/
		w.syntacticTree= pipeArray[14];
		w.extra2= pipeArray[15];
		w.extra3= pipeArray[16];
		w.extra4= pipeArray[17];
		w.extra5= pipeArray[18];
		w.extra6= pipeArray[19];
		/*w.element_type= pipeArray[20];
		w.element_type_class= pipeArray[21];*/
		return true;

	}

	@Override
	public boolean isSentenceEnd(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getExtension() {
		return "classik";
	}

}
