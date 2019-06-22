package com.bizlem.drools.model;

import java.util.List;

import com.google.common.collect.Multimap;

public class ExtractedData {

  private List<Rules> rules;
  private Multimap<String, String> variableNameToDataType;
  private String drlName;
  
  
public ExtractedData() {
	
}
public List<Rules> getRules() {
	return rules;
}
public void setRules(List<Rules> rules) {
	this.rules = rules;
}
public Multimap<String, String> getVariableNameToDataType() {
	return variableNameToDataType;
}
public void setVariableNameToDataType(Multimap<String, String> variableNameToDataType) {
	this.variableNameToDataType = variableNameToDataType;
}
public String getDrlName() {
	return drlName;
}
public void setDrlName(String drlName) {
	this.drlName = drlName;
}
  
}
