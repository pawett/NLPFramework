package com.NLPFramework.Formatters;

import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;

public class BaseSegmentationFormatter implements IWordFormatter {
	/*file|sent-num|tok-num|word|pos|syntbio|sentence|synt|verb|
	 * lemma|wn|roleconf|simplerolesIOB2|simplerolesIOB2_verb|
	 * simpleroles|depverb|tense|assertype|iobmainphrase|mainp-position|phra_id|PPdetail
	 */
	public String toString(Word w) {
	
		StringBuilder sb = new StringBuilder();
		 sb.append(w.file);
		 sb.append(FileHelper.AppendTabs(w.sentenceNumber));
		 sb.append(FileHelper.AppendTabs(w.sentencePosition));
		 sb.append(FileHelper.AppendTabs(w.word));
		
		 return sb.toString();
	}

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
