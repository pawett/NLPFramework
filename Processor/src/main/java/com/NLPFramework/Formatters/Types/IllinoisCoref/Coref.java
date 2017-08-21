package com.NLPFramework.Formatters.Types.IllinoisCoref;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;



@JsonPropertyOrder({
"nodes",
"links"
})
public class Coref implements Serializable
{

@JsonProperty("nodes")
private List<Node> nodes = new ArrayList<Node>();
@JsonProperty("links")
private List<Link> links = new ArrayList<Link>();
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -691030056837858513L;

/**
* No args constructor for use in serialization
* 
*/
public Coref() {
}

/**
* 
* @param nodes
* @param links
*/
public Coref(List<Node> nodes, List<Link> links) {
super();
this.nodes = nodes;
this.links = links;
}

@JsonProperty("nodes")
public List<Node> getNodes() {
return nodes;
}

@JsonProperty("nodes")
public void setNodes(List<Node> nodes) {
this.nodes = nodes;
}

public Coref withNodes(List<Node> nodes) {
this.nodes = nodes;
return this;
}

@JsonProperty("links")
public List<Link> getLinks() {
return links;
}

@JsonProperty("links")
public void setLinks(List<Link> links) {
this.links = links;
}

public Coref withLinks(List<Link> links) {
this.links = links;
return this;
}

@JsonAnyGetter
public Map<String, Object> getAdditionalProperties() {
return this.additionalProperties;
}

@JsonAnySetter
public void setAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
}

public Coref withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}
