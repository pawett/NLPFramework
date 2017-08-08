package com.NLPFramework.RESTClient;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.client.ClientConfig;

public class ClientBase {

	private String baseSource = "http://model.dbpedia-spotlight.org/en/annotate";
	private WebTarget webResource;
	public ClientBase()
	{
		ClientConfig clientConfig = new ClientConfig();
		clientConfig.register(JacksonJsonProvider.class);
		Client client = ClientBuilder.newClient(clientConfig);
	
		webResource = client.target(baseSource);

	}
	
	public Response get(String text)
	{
		
		try{
			
			webResource = webResource.queryParam("text", URLEncoder.encode(text, "UTF-8"));
			Response response = webResource.request(MediaType.APPLICATION_JSON_TYPE)
					.get();

			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}

			
			
		
			
			/*if(output.getAdditionalProperties().get("Resources") != null && !output.getAdditionalProperties().get("Resources").toString().isEmpty())
			{
				String resources = output.getAdditionalProperties().get("Resources").toString();
				JsonFactory factory = new JsonFactory();
				ObjectMapper mapper = new ObjectMapper(factory);
				Resource[] res = mapper.readValue(resources, Resource[].class);
			}*/

			
			return response;

		} catch (Exception e) {

			e.printStackTrace();

		}
		return null;
	}
}
