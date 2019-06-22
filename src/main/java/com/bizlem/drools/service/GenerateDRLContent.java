package com.bizlem.drools.service;

import java.util.List;
import java.util.Map;
import com.bizlem.drools.model.Rules;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.collect.Multimap;

public interface GenerateDRLContent {

  String initDrlContent(List<Rules> ruleList, Multimap<String, String> variableNameToDataType) throws JsonProcessingException;

}
