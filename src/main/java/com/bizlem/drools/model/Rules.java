package com.bizlem.drools.model;


import com.google.common.collect.Multimap;



public class Rules {
  private String ruleName;
  private Multimap<String, String> inputFieldPair;
  private Multimap<String, String> outputFiledPair;
public Rules() {}
public Rules(String ruleName, Multimap<String, String> inputFieldPair, Multimap<String, String> outputFiledPair) {
	this.ruleName = ruleName;
	this.inputFieldPair = inputFieldPair;
	this.outputFiledPair = outputFiledPair;
}
public String getRuleName() {
	return ruleName;
}
public void setRuleName(String ruleName) {
	this.ruleName = ruleName;
}
public Multimap<String, String> getInputFieldPair() {
	return inputFieldPair;
}
public void setInputFieldPair(Multimap<String, String> inputFieldPair) {
	this.inputFieldPair = inputFieldPair;
}
public Multimap<String, String> getOutputFiledPair() {
	return outputFiledPair;
}
public void setOutputFiledPair(Multimap<String, String> outputFiledPair) {
	this.outputFiledPair = outputFiledPair;
}
  
}
