package com.NLPFramework.Processor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.Coreference;
import com.NLPFramework.Domain.EntityType;
import com.NLPFramework.Domain.NER;
import com.NLPFramework.Domain.NERCoreference;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.NERCoreferenceHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.TimeMLFile;

public class ActionProcessNER extends ActionNERBase {

	@Override
	public TokenizedFile execute(TokenizedFile tokFile)
	{
		TimeMLFile file = (TimeMLFile) tokFile;
		annotateNERs(file);
		annotateCoreferences(file);
		mergeNERCoreferences(file);
		return tokFile;
	}

	
	private void annotateCoreferences(TimeMLFile file) 
	{
		for(TokenizedSentence A0 : file)
		{
			TokenizedSentence A0Coref = new TokenizedSentence();
			for(Word w : A0)
			{
				//if(!w.pos.equals("PRP"))
				//	continue;
				NER mainCorefNER = null;
				EntityMapper<Annotation> an = null;
				an = A0.annotations.get(NER.class) != null ? A0.annotations.get(NER.class).get(w) : null;
				if(an != null)
					mainCorefNER = (NER)an.element;
				else
				{
					Coreference cr = NERCoreferenceHelper.getMainReference(file, w);
					if(cr == null)
						continue;

					Logger.Write("Coreference:: " + cr.toString());
					mainCorefNER = NERCoreferenceHelper.getMainNerFromCoreference(file, cr);
				}
				if(mainCorefNER != null)
				{
					Logger.Write("NER:: " + mainCorefNER);
				

					EntityMapper<NER> map = new EntityMapper<>();
					NER ner = new NER();
					ner.word = w;
					ner.entityName = mainCorefNER.entityName;
					ner.type = mainCorefNER.type;
					map.element = ner;
					map.firstWordPosition = w.sentencePosition;
					map.endWordPosition = w.sentencePosition;

					NERCoreferenceHelper.addCoreference(file, map);
					
					
					/*ArrayList<NER> nersInCoreference = new ArrayList<>();

					Word next = cr.word;
					TokenizedSentence mainCorefSentence = TimeMLHelper.getWordSentence(file, next);

					for(int cri = 0; next != null && cri < cr.offset; cri++)
					{
						EntityMapper<Annotation> mainCorefNerAnnotation = mainCorefSentence.annotations.get(NER.class).get(next);
						if(mainCorefNerAnnotation == null)
							continue;
						NER currentCoreferenceNer = (NER)mainCorefNerAnnotation.element;

						nersInCoreference.add(currentCoreferenceNer);
						if(next.pos.startsWith("NN"))
						A0Coref.add(next);
						next = next.next;

					}*/

				//	A0Coref.add(w);
					/*file.annotations.get(NERCoreference.class);
				for(int cri = 0; next != null && cri < cr.offset; cri++)
				{
					if(next.pos.startsWith("NN"))
					A0Coref.add(next);
					next = next.next;

				}*/
				//	Logger.Write("Coreference for " + w.word + " :: " + NERCoreferenceHelper.getMainReference(file, w).toString());
				}
			}
		}
		
	}

	private void annotateNERs(TimeMLFile file) 
	{
		for(TokenizedSentence sentence : file)
		{
			int lastNERposition = -1;
			TokenizedSentence A0NER = new TokenizedSentence();
			for(Word w : sentence)
			{
				TokenizedSentence currentWordSentence = TimeMLHelper.getWordSentence(file, w);
				if(w.sentencePosition <= lastNERposition)
					continue;

				if(currentWordSentence.annotations.get(NER.class) != null && currentWordSentence.annotations.get(NER.class).get(w) != null)
				{
					EntityMapper<Annotation> nerAnnotation = currentWordSentence.annotations.get(NER.class).get(w);
					lastNERposition = nerAnnotation.endWordPosition;
					//Logger.Write("NER found " + ((NER)nerAnnotation.element).entityName + " for word " + w + " in " + sentence.toString());
				}
				else
				{

					if(w.ner == EntityType.PERSON || w.ner == EntityType.ORGANIZATION)
					{
						int a = 0;
						Word next = w.next;
						String name = w.word;
						while(next != null && next.ner != null && next.ner.equals(w.ner))
						{
							name = name + "_" + next.word;
							next = next.next;
						}
						EntityMapper<NER> map = new EntityMapper<>();
						NER ner = new NER();
						ner.word = w;
						ner.entityName = name;
						ner.type = w.ner;
						map.element = ner;
						map.firstWordPosition = w.sentencePosition;
						map.endWordPosition = next != null ? next.sentencePosition - 1 : w.sentencePosition;
						lastNERposition = map.endWordPosition;
						//Logger.Write("NER Word found:: " + ner.entityName);

						NERCoreferenceHelper.addCoreference(file, map);


						currentWordSentence.addAnnotation(NER.class, w, map);
					}

				}
			}
		}
	}
	
	
	
	
	private void mergeNERCoreferences(TimeMLFile file)
	{
		LinkedList<Annotation> annotations = file.getAnnotations(NERCoreference.class);
		LinkedList<Annotation> annotationsToDelete = new LinkedList<>();
		if(annotations != null)
		{
			for(Annotation currentAnnotation : annotations)
			{
				NERCoreference currentNerAnnotation = (NERCoreference) currentAnnotation;
			//	Logger.Write("NER Coref: " + currentNerAnnotation.toString());
				List<Annotation> similarAnnotations = annotations.stream().filter(n -> !n.equals(currentAnnotation) &&
						(((NERCoreference)n).mainCoref.element.entityName.toLowerCase().contains(currentNerAnnotation.mainCoref.element.entityName.toLowerCase()) ) ) //|| nercoref.mainCoref.element.entityName.contains(((NERCoreference)n).mainCoref.element.entityName
						.collect(Collectors.toList());
				if(similarAnnotations != null)
				{
					for(Annotation similarAnnotation : similarAnnotations)
					{
						NERCoreference coref = (NERCoreference) similarAnnotation;
						//
						if(coref.mainCoref.element.type != null 
								&& currentNerAnnotation.mainCoref.element.type != null
								&& coref.mainCoref.element.type.equals(currentNerAnnotation.mainCoref.element.type)
								&& currentNerAnnotation.mainCoref.element.type.equals(EntityType.PERSON))
						{
							if(coref.mainCoref.element.word.sentenceNumber <= currentNerAnnotation.mainCoref.element.word.sentenceNumber)
							{
								currentNerAnnotation.mainCoref.element.entityName = coref.mainCoref.element.entityName;
								coref.addCoref(currentNerAnnotation.mainCoref);

								for(EntityMapper<NER> subCoref : currentNerAnnotation.coreferences)
								{
									subCoref.element.entityName = coref.mainCoref.element.entityName;
									if(subCoref.element.word.sentenceNumber >= currentNerAnnotation.mainCoref.element.word.sentenceNumber)
										coref.addCoref(subCoref);
								}
								
								annotationsToDelete.add(currentAnnotation);
																
							}
							//Logger.Write("NER Coref: " + currentNerAnnotation.toString() + " is similar to " + similarAnnotation.toString());
						}
					}
				}
			}
			
			
		}
		
		for(Annotation annotationToDelete : annotationsToDelete)
		{
			file.getAnnotations(NERCoreference.class).remove(annotationToDelete);
		}
		
		annotations = file.getAnnotations(NERCoreference.class);
		for(Annotation currentAnnotation : annotations)
		{
			NERCoreference currentNerAnnotation = (NERCoreference) currentAnnotation;
			//Logger.Write("NER Coref: " + currentNerAnnotation.toString());
		}
	}
	
}
