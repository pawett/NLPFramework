package com.NLPFramework.RESTClient;
import java.net.URLEncoder;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
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
			String textEncoded =  URLEncoder.encode(text, "UTF-8");
			
			webResource = webResource.queryParam("text", text);
			webResource = webResource.queryParam("confidence", URLEncoder.encode("0.45", "UTF-8"));
			webResource = webResource.queryParam("support", URLEncoder.encode("0", "UTF-8"));
			webResource = webResource.queryParam("spotter", "Default");
			webResource = webResource.queryParam("disambiguator", "Default");
			
			//DBpedia:Device,DBpedia:Organisation,DBpedia:Person,DBpedia:Work
			//webResource = webResource.queryParam("policy", "blacklist");
			//webResource = webResource.queryParam("types", URLEncoder.encode("Schema:Event,Schema:Language,Schema:Place", "UTF-8"));
			webResource = webResource.queryParam("sparql", "");
			
			Response response = webResource.request(MediaType.APPLICATION_JSON).header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36")
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
