package com.bizlem.drools.service;

import java.io.IOException;

public interface ExtractDataFromJson {
  void extractRulesAndVariableInfo(String inputJson) throws IOException;
}
