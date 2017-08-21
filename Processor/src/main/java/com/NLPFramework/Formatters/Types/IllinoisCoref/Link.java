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
"source",
"target"
})
public class Link implements Serializable
{

@JsonProperty("source")
private int source;
@JsonProperty("target")
private int target;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = -3897822271878094642L;

/**
* No args constructor for use in serialization
* 
*/
public Link() {
}

/**
* 
* @param source
* @param target
*/
public Link(int source, int target) {
super();
this.source = source;
this.target = target;
}

@JsonProperty("source")
public int getSource() {
return source;
}

@JsonProperty("source")
public void setSource(int source) {
this.source = source;
}

public Link withSource(int source) {
this.source = source;
return this;
}

@JsonProperty("target")
public int getTarget() {
return target;
}

@JsonProperty("target")
public void setTarget(int target) {
this.target = target;
}

public Link withTarget(int target) {
this.target = target;
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

public Link withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}

}
