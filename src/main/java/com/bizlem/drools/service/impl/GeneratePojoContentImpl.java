package com.bizlem.drools.service.impl;

import java.util.Map;
import org.springframework.stereotype.Service;
import com.bizlem.drools.service.GeneratePojoContent;

@Service
public class GeneratePojoContentImpl implements GeneratePojoContent {

  @Override
  public Map<String, String> getExistingVariables() {

    return null;
  }

  @Override
  public String initPojoContent(Map<String, String> variableNameToDataType) {

    StringBuffer buffer = new StringBuffer();
    buffer.append("package com.bizlem.drools.model; \n" + "import lombok.Data;\r\n" + "import com.fasterxml.jackson.annotation.JsonIgnoreProperties;\r\n" + "import lombok.NoArgsConstructor;\r\n" + "@Data \r\n"
        + "@NoArgsConstructor \r\n" + "@JsonIgnoreProperties(ignoreUnknown = true) \r\n");

    buffer.append("public class VariablePOJO \n { \n");
    // Add all variable to the buffer
    variableNameToDataType.forEach((k, v) -> buffer.append(appendString(v, k)));
    buffer.append("\n}");

    return buffer.toString();
  }

  private String appendString(String dataType, String variableName) {
    return "public " + dataType + " " + variableName + "; \n";
  }
}
