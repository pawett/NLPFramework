package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class TempEval2FeaturesFormatter implements IWordFormatter {
	/*file|sent-num|tok-num|word|pos|syntbio|sentence|synt|verb|
	 * lemma|wn|roleconf|simplerolesIOB2|simplerolesIOB2_verb|
	 * simpleroles|depverb|tense|assertype|iobmainphrase|mainp-position|phra_id|PPdetail
	 */
	@Override
	public String toString(Word w) {
		String [] synt = w.syntbio.split("-");
		if(synt.length >= 1)
			w.syntbio = synt[0];
		if(synt.length >= 2)
			w.synt = synt[1];
		String sentenceNumber = String.valueOf(w.sentenceNumber);
		StringBuilder sb = new StringBuilder();
		 sb.append(w.file);
		 sb.append(PipesHelper.AppendPipes(sentenceNumber));
		 sb.append(PipesHelper.AppendPipes(w.sentencePosition));
		 sb.append(PipesHelper.AppendPipes(w.word));
		 sb.append(PipesHelper.AppendPipes(w.pos));
		 sb.append(PipesHelper.AppendPipes(w.syntbio));
		 sb.append(PipesHelper.AppendPipes(w.sentence));
		 sb.append(PipesHelper.AppendPipes(w.synt));
		 sb.append(PipesHelper.AppendPipes("-"));
		 sb.append(PipesHelper.AppendPipes(w.lemma));
		 sb.append(PipesHelper.AppendPipes(w.wn));
		 String roleConf = w.roleconf;
		 Word prev = w.prev;
		/* while(prev != null && w.sentence.equals(prev.sentence))
		 {
			 roleConf += prev.synt;
			 prev = prev.prev;
		 }*/
		 
		 sb.append(PipesHelper.AppendPipes(roleConf));
		 /*SemanticRole sr = w.getSemanticRole();
		 if(sr != null)
		 {
			 sb.append(PipesHelper.AppendPipes(sr.IOB));
			 if(sr.verb != null)
				 sb.append(PipesHelper.AppendPipes(sr.verb.lemma));
			 else  sb.append(PipesHelper.AppendPipes("-"));
			 if(sr.argument != null)
				 sb.append(PipesHelper.AppendPipes(sr.argument.toString()));
			 else  sb.append(PipesHelper.AppendPipes("-"));
			 
			 if(sr.verb != null)
			 {
			 sb.append(PipesHelper.AppendPipes(sr.verb.lemma));
			 sb.append(PipesHelper.AppendPipes(sr.verb.tense));
			 sb.append(PipesHelper.AppendPipes(sr.verb.polarity));
			 }
			 else
			 {
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
			 }
			 
			 
		 }else
		 {*/
			 sb.append(PipesHelper.AppendPipes(w.semanticRoleIOB));
			 if(w.depverb!= null)
				 sb.append(PipesHelper.AppendPipes(w.depverb.lemma));
			 else
				 sb.append(PipesHelper.AppendPipes("-"));
			 
			 sb.append(PipesHelper.AppendPipes(w.getSemanticRole()));
			 if(w.depverb!= null)
			 {
				 sb.append(PipesHelper.AppendPipes(w.depverb.lemma));
				 sb.append(PipesHelper.AppendPipes(w.depverb.tense));
				 sb.append(PipesHelper.AppendPipes(w.depverb.polarity));
			 }else
			 {
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
				 sb.append(PipesHelper.AppendPipes("-"));
			 }
			 
			
		 //}
		 sb.append(PipesHelper.AppendPipes(w.mainphraseIOB));
		 sb.append(PipesHelper.AppendPipes(w.mainp_position));
		 sb.append(PipesHelper.AppendPipes(w.phra_id));
		 sb.append(PipesHelper.AppendPipes(w.preposition));
		 return sb.toString();
	}

	@Override
	public boolean setValues(Word word, String values) 
	{
		/*String[] pipesArray = values.split("\\|");
		word.file = pipesArray[0];
		word.sent_num = pipesArray[1];
		word.tok_num= pipesArray[2];
		word.word = pipesArray[3];
		word.pos = pipesArray[4];
		word.syntbio = pipesArray[5];
		word.sentence= pipesArray[6];
		word.synt= pipesArray[7];
		word.verb= pipesArray[8];
		word.lemma= pipesArray[9];
		word.wn= pipesArray[10];
		word.roleconf= pipesArray[11];
		SemanticRole sr = new SemanticRole();
		sr.IOB = pipesArray[12];
		sr.verb.word= pipesArray[13];
		sr.argument= PropBankArgument.valueOf(pipesArray[14]);
		word.semanticRoles.add(sr);
		word.depverb= pipesArray[15];
		word.tense= pipesArray[16];
		word.polarity= pipesArray[17];
		word.mainphraseIOB= pipesArray[18];
		word.mainp_position= pipesArray[19];
		word.phra_id= pipesArray[20];
		word.preposition= pipesArray[21];*/
		return true;
	}

	@Override
	public boolean isSentenceEnd(String text) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getExtension() {
		// TODO Auto-generated method stub
		return "TempEval2-features";
	}

}
