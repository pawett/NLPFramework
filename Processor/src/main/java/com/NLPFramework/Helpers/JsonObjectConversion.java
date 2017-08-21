package com.NLPFramework.Helpers;

import java.io.IOException;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.NLPFramework.Crosscutting.Logger;

public class JsonObjectConversion {
	
	public <T> T deserialize(Class<T> classType, String json)
	{
		T deserializedObject = null;
		ObjectMapper mapper = new ObjectMapper();
		
		try {
			deserializedObject = mapper.readValue(json, classType);
		} catch (JsonParseException e) 
		{
			Logger.WriteError("Parse Error on deserializing", e);
		} catch (JsonMappingException e) {
			Logger.WriteError("Mapping Error on deserializing", e);
		} catch (IOException e) {
			Logger.WriteError("IO Error on deserializing", e);
		}
		
		
		return deserializedObject;
	}
	
	public <T> String serialize(T object)
	{
		String json = null;
		ObjectMapper mapper = new ObjectMapper();
		try 
		{
			json = mapper.writeValueAsString(object);
		} catch (IOException e)
		{			
			Logger.WriteError("IO Error on serializing", e);
		}
		
		return json;
	}

}
