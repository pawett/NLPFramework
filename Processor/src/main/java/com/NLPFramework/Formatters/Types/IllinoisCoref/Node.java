package com.NLPFramework.Formatters.Types.IllinoisCoref;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({
"id",
"name",
"text",
"MType"
})
public class Node implements Serializable
{

@JsonProperty("id")
private int id;
@JsonProperty("name")
private String name;
@JsonProperty("text")
private String text;
@JsonProperty("MType")
private String mType;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -5662731670491331784L;

/**
* No args constructor for use in serialization
* 
*/
public Node() {
}

/**
* 
* @param mType
* @param id
* @param text
* @param name
*/
public Node(int id, String name, String text, String mType) {
super();
this.id = id;
this.name = name;
this.text = text;
this.mType = mType;
}

@JsonProperty("id")
public int getId() {
return id;
}

@JsonProperty("id")
public void setId(int id) {
this.id = id;
}

public Node withId(int id) {
this.id = id;
return this;
}

@JsonProperty("name")
public String getName() {
return name;
}

@JsonProperty("name")
public void setName(String name) {
this.name = name;
}

public Node withName(String name) {
this.name = name;
return this;
}

@JsonProperty("text")
public String getText() {
return text;
}

@JsonProperty("text")
public void setText(String text) {
this.text = text;
}

public Node withText(String text) {
this.text = text;
return this;
}

@JsonProperty("MType")
public String getMType() {
return mType;
}

@JsonProperty("MType")
public void setMType(String mType) {
this.mType = mType;
}

public Node withMType(String mType) {
this.mType = mType;
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

public Node withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}
