package com.NLPFramework.RESTClient;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.annotate.JsonAnyGetter;
import org.codehaus.jackson.annotate.JsonAnySetter;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.annotate.JsonPropertyOrder;

@JsonPropertyOrder({
"@URI",
"@support",
"@types",
"@surfaceForm",
"@offset",
"@similarityScore",
"@percentageOfSecondRank"
})
public class Resource implements Serializable
{

@JsonProperty("@URI")
private String uRI;
@JsonProperty("@support")
private String support;
@JsonProperty("@types")
private String types;
@JsonProperty("@surfaceForm")
private String surfaceForm;
@JsonProperty("@offset")
private String offset;
@JsonProperty("@similarityScore")
private String similarityScore;
@JsonProperty("@percentageOfSecondRank")
private String percentageOfSecondRank;
@JsonIgnore
private Map<String, Object> additionalProperties = new HashMap<String, Object>();
private final static long serialVersionUID = 2465138216459101071L;

/**
* No args constructor for use in serialization
* 
*/
public Resource() {
}

/**
* 
* @param support
* @param percentageOfSecondRank
* @param uRI
* @param surfaceForm
* @param offset
* @param types
* @param similarityScore
*/
public Resource(String uRI, String support, String types, String surfaceForm, String offset, String similarityScore, String percentageOfSecondRank) {
super();
this.uRI = uRI;
this.support = support;
this.types = types;
this.surfaceForm = surfaceForm;
this.offset = offset;
this.similarityScore = similarityScore;
this.percentageOfSecondRank = percentageOfSecondRank;
}

@JsonProperty("@URI")
public String getURI() {
return uRI;
}

@JsonProperty("@URI")
public void setURI(String uRI) {
this.uRI = uRI;
}

public Resource withURI(String uRI) {
this.uRI = uRI;
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

public Resource withSupport(String support) {
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

public Resource withTypes(String types) {
this.types = types;
return this;
}

@JsonProperty("@surfaceForm")
public String getSurfaceForm() {
return surfaceForm;
}

@JsonProperty("@surfaceForm")
public void setSurfaceForm(String surfaceForm) {
this.surfaceForm = surfaceForm;
}

public Resource withSurfaceForm(String surfaceForm) {
this.surfaceForm = surfaceForm;
return this;
}

@JsonProperty("@offset")
public String getOffset() {
return offset;
}

@JsonProperty("@offset")
public void setOffset(String offset) {
this.offset = offset;
}

public Resource withOffset(String offset) {
this.offset = offset;
return this;
}

@JsonProperty("@similarityScore")
public String getSimilarityScore() {
return similarityScore;
}

@JsonProperty("@similarityScore")
public void setSimilarityScore(String similarityScore) {
this.similarityScore = similarityScore;
}

public Resource withSimilarityScore(String similarityScore) {
this.similarityScore = similarityScore;
return this;
}

@JsonProperty("@percentageOfSecondRank")
public String getPercentageOfSecondRank() {
return percentageOfSecondRank;
}

@JsonProperty("@percentageOfSecondRank")
public void setPercentageOfSecondRank(String percentageOfSecondRank) {
this.percentageOfSecondRank = percentageOfSecondRank;
}

public Resource withPercentageOfSecondRank(String percentageOfSecondRank) {
this.percentageOfSecondRank = percentageOfSecondRank;
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

public Resource withAdditionalProperty(String name, Object value) {
this.additionalProperties.put(name, value);
return this;
}



}