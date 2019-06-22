package com.bizlem.drools.service;

import java.util.Map;

public interface GeneratePojoContent {

  String initPojoContent(Map<String, String> variableNameToDataType);

  Map<String, String> getExistingVariables();

}
