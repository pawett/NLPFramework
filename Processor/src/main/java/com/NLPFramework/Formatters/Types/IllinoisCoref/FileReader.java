package com.NLPFramework.Formatters.Types.IllinoisCoref;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.codehaus.jackson.map.ObjectMapper;

public class FileReader 
{
	public IllinoisCoreference readFile(String path)
	{
		File file = new File(path);
		List<String> lines = null;
		 try 
		 {
			lines = Files.readAllLines(file.toPath());
			ObjectMapper mapper = new ObjectMapper();
			StringBuilder sb = new StringBuilder();
			lines.forEach(l -> sb.append(l));
			IllinoisCoreference coref = mapper.readValue(sb.toString() , IllinoisCoreference.class);
			return coref;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}

}
