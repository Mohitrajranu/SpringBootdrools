package com.bizlem.drools.service;

import java.io.IOException;
import java.util.Map;

public interface RuleCallingService {

    Map<String, String> callRules(String drlName, Map<String, String> variablePOJO) throws IOException;

}
