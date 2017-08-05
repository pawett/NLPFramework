package com.NLPFramework.Formatters;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.SemanticRole;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.PipesHelper;

public class FeaturesFormatter implements ISentenceFormatter {

	@Override
	public String toString(TokenizedSentence sentence) 
	{
		StringBuilder sb = new StringBuilder();
		
		//Logger.Write(s.toStringSyntFlat());
		int verbPos = 0;
		for(Word v : sentence.verbs)
		{
			//Logger.Write("Sentence for " + v.word);
			for(Word w : sentence)
			{
				if(w.semanticRoles != null && w.semanticRoles.get(verbPos) != null && w.semanticRoles.get(verbPos).argument != null)
				{
					sb.append(this.toString(w, v, w.semanticRoles.get(verbPos)));
					sb.append(System.lineSeparator());
				}
			}
			verbPos++;
			sb.append(System.lineSeparator());
		}
		/*for(Word w : sentence)
		{
			sb.append(this.toString(w, sentence.getWordDependantVerb(w)));
			//if(w.synt.equalsIgnoreCase("SBAR"))
			//sb.append(System.lineSeparator());
			
			sb.append(System.lineSeparator());
		}*/
		//sb.append(System.lineSeparator());
		return sb.toString();
	}
	
	public String toString(Word w, Word depVerb, SemanticRole s) 
	{
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
		sb.append(PipesHelper.AppendPipes(w.isVerb));
		sb.append(PipesHelper.AppendPipes(w.synt));
		sb.append(PipesHelper.AppendPipes("-"));
		sb.append(PipesHelper.AppendPipes(w.lemma));
		sb.append(PipesHelper.AppendPipes(w.wn));
		String roleConf = w.roleconf;

		sb.append(PipesHelper.AppendPipes(roleConf));

		sb.append(PipesHelper.AppendPipes(s.IOB));
		
		if(depVerb!= null)
			sb.append(PipesHelper.AppendPipes(depVerb.lemma));
		else
			sb.append(PipesHelper.AppendPipes("-"));

		sb.append(PipesHelper.AppendPipes(s.argument));
		if(depVerb != null)
		{
			sb.append(PipesHelper.AppendPipes(depVerb.lemma));
			sb.append(PipesHelper.AppendPipes(depVerb.tense));
			sb.append(PipesHelper.AppendPipes(depVerb.polarity));
		}else
		{
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
			sb.append(PipesHelper.AppendPipes("-"));
		}

		sb.append(PipesHelper.AppendPipes(w.mainphraseIOB));
		sb.append(PipesHelper.AppendPipes(w.phra_id));
		sb.append(PipesHelper.AppendPipes(w.preposition));
		return sb.toString();
	}

	public String toString(Word w) 
	{
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
		sb.append(PipesHelper.AppendPipes(w.isVerb));
		sb.append(PipesHelper.AppendPipes(w.synt));
		sb.append(PipesHelper.AppendPipes("-"));
		sb.append(PipesHelper.AppendPipes(w.lemma));
		sb.append(PipesHelper.AppendPipes(w.wn));
		String roleConf = w.roleconf;

		sb.append(PipesHelper.AppendPipes(roleConf));

		sb.append(PipesHelper.AppendPipes(w.semanticRoleIOB));
		
		if(w.depverb!= null)
			sb.append(PipesHelper.AppendPipes(w.depverb.lemma));
		else
			sb.append(PipesHelper.AppendPipes("-"));

		sb.append(PipesHelper.AppendPipes(w.semanticRole));
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

		sb.append(PipesHelper.AppendPipes(w.mainphraseIOB));
		sb.append(PipesHelper.AppendPipes(w.phra_id));
		sb.append(PipesHelper.AppendPipes(w.preposition));

		return sb.toString();
	}

	
	@Override
	public String getExtension() {
		return "features";
	}

	@Override
	public void setValues(TokenizedSentence sentence, String values, Word firstElement) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String toString(TokenizedSentence s, Word w) {
		// TODO Auto-generated method stub
		return null;
	}

}
