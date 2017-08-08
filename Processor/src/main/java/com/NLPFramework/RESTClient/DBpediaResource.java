package com.NLPFramework.RESTClient;

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
"@text",
"@confidence",
"@support",
"@types",
"@sparql",
"@policy",
"Resources"
})
public class DBpediaResource implements Serializable
{

@JsonProperty("@text")
private String text;
@JsonProperty("@confidence")
private String confidence;
@JsonProperty("@support")
private String support;
@JsonProperty("@types")
private String types;
@JsonProperty("@sparql")
private String sparql;
@JsonProperty("@policy")
private String policy;
@JsonProperty("Resources")
private List<Resource> resources = new ArrayList<Resource>();
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -1846384564768885236L;

/**
* No args constructor for use in serialization
* 
*/
public DBpediaResource() {
}

/**
* 
* @param resources
* @param text
* @param support
* @param policy
* @param confidence
* @param types
* @param sparql
*/
public DBpediaResource(String text, String confidence, String support, String types, String sparql, String policy, List<Resource> resources) {
super();
this.text = text;
this.confidence = confidence;
this.support = support;
this.types = types;
this.sparql = sparql;
this.policy = policy;
this.resources = resources;
}

@JsonProperty("@text")
public String getText() {
return text;
}

@JsonProperty("@text")
public void setText(String text) {
this.text = text;
}

public DBpediaResource withText(String text) {
this.text = text;
return this;
}

@JsonProperty("@confidence")
public String getConfidence() {
return confidence;
}

@JsonProperty("@confidence")
public void setConfidence(String confidence) {
this.confidence = confidence;
}

public DBpediaResource withConfidence(String confidence) {
this.confidence = confidence;
return this;
}

@JsonProperty("@support")
public String getSupport() {
return support;
}

@JsonProperty("@support")
public void setSupport(String support) {
this.support = support;
}

public DBpediaResource withSupport(String support) {
this.support = support;
return this;
}

@JsonProperty("@types")
public String getTypes() {
return types;
}

@JsonProperty("@types")
public void setTypes(String types) {
this.types = types;
}

public DBpediaResource withTypes(String types) {
this.types = types;
return this;
}

@JsonProperty("@sparql")
public String getSparql() {
return sparql;
}

@JsonProperty("@sparql")
public void setSparql(String sparql) {
this.sparql = sparql;
}

public DBpediaResource withSparql(String sparql) {
this.sparql = sparql;
return this;
}

@JsonProperty("@policy")
public String getPolicy() {
return policy;
}

@JsonProperty("@policy")
public void setPolicy(String policy) {
this.policy = policy;
}

public DBpediaResource withPolicy(String policy) {
this.policy = policy;
return this;
}

@JsonProperty("Resources")
public List<Resource> getResources() {
return resources;
}

@JsonProperty("Resources")
public void setResources(List<Resource> resources) {
this.resources = resources;
}

public DBpediaResource withResources(List<Resource> resources) {
this.resources = resources;
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

public DBpediaResource withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}
}