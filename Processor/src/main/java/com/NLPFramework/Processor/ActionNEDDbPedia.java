package com.NLPFramework.Processor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Optional;

import javax.ws.rs.core.Response;

import com.NLPFramework.Crosscutting.Logger;
import com.NLPFramework.Domain.Annotation;
import com.NLPFramework.Domain.EntityType;
import com.NLPFramework.Domain.NER;
import com.NLPFramework.Domain.NERCoreference;
import com.NLPFramework.Domain.TokenizedFile;
import com.NLPFramework.Domain.TokenizedSentence;
import com.NLPFramework.Domain.Word;
import com.NLPFramework.Helpers.FileHelper;
import com.NLPFramework.Helpers.NERCoreferenceHelper;
import com.NLPFramework.Helpers.TimeMLHelper;
import com.NLPFramework.RESTClient.ClientBase;
import com.NLPFramework.RESTClient.DBpediaResource;
import com.NLPFramework.RESTClient.Resource;
import com.NLPFramework.TimeML.Domain.EntityMapper;
import com.NLPFramework.TimeML.Domain.TimeMLFile;
import com.NLPFramework.externalTools.StanfordSynt;

public class ActionNEDDbPedia implements  INLPActionFile {

	
	public ArrayList<Class<? extends INLPActionFile>> getDependencies() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TokenizedFile execute(TokenizedFile tokFile) throws Exception 
	{
		Logger.WriteDebug("Executing DBPEDIA");
		for(TokenizedSentence sentence : tokFile)
		{
			ClientBase base = new ClientBase();
			Response response = base.get(sentence.getOriginalText());
			//String returnString = response.readEntity(String.class);
			DBpediaResource output = response.readEntity(DBpediaResource.class);
			
			//Logger.Write("Sentence: " + sentence.originalText);
			for(Resource r : output.getResources())
			{
				LinkedList<String> resourceWords = StanfordSynt.tokenize(FileHelper.formatText(r.getSurfaceForm()));
				
				//Word w =sentence.get(Integer.parseInt(r.getOffset()));
				//Logger.Write("NER detected: "+ r.getURI() + " Word: " + r.getOffset() + " " + r.getTypes());
				Word w = TimeMLHelper.getWordFromCharacterPositionAndWord(sentence, Integer.valueOf(r.getOffset()), resourceWords.getFirst());
				if(w != null)
				{
					NER ner = new NER();
					ner.entityName = r.getURI().substring(r.getURI().lastIndexOf("/") + 1);
					if(r.getTypes().contains("Person"))
						ner.type = EntityType.PERSON;
					else if(r.getTypes().contains("Organization"))
						ner.type = EntityType.ORGANIZATION;
					else if(r.getTypes().contains("Product") || r.getTypes().contains("Work") || r.getTypes().contains("Device"))
						ner.type = EntityType.PRODUCT;
					else if(r.getTypes().contains("Currency"))
						ner.type = EntityType.FINANCIAL;
					else
						continue;
					
					ner.word = w;
					
					w.ner = ner.type != null ? ner.type : null;
					
					EntityMapper<NER> map = new EntityMapper<>();
					map.element = ner;
					map.firstWordPosition = w.sentencePosition;
					map.endWordPosition = w.sentencePosition + resourceWords.size() - 1;
				//	Logger.Write("Word found:: " + w.word);
					
					NERCoreferenceHelper.addCoreference((TimeMLFile) tokFile, map);
					sentence.addAnnotation(NER.class, w, map);
			
				}else 
					Logger.Write("No word found");
			}
		}
		return tokFile;
	}

}
