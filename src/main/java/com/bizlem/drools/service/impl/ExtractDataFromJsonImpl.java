package com.bizlem.drools.service.impl;

import com.bizlem.drools.model.ExtractedData;
import com.bizlem.drools.model.Rules;
import com.bizlem.drools.service.ExtractDataFromJson;
import com.bizlem.drools.service.GenerateDRLContent;
import com.bizlem.drools.util.ReadFile;
import com.bizlem.drools.util.WriteContentToFile;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class ExtractDataFromJsonImpl implements ExtractDataFromJson {
	private static Logger log = LoggerFactory.getLogger(ExtractDataFromJsonImpl.class);
    //private final String drlPath = System.getProperty("com.bizlem.appDir") + File.separator;
    @Autowired
    private GenerateDRLContent drlContent;
    @Value("${drools.drlFilePath}")
    private String drlPath;
    @Value("${drools.PojoFilepath}")
    private String pojoPath;

    private final ObjectMapper objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public void extractRulesAndVariableInfo(String inputJson) throws IOException {
        log.info("Path of the folder that contains rules file: {}", drlPath);
        ExtractedData extractedData = null;
        Multimap<String, String> existingVariableNameToDatatype;
        final String POJO_NAME = "VariablePOJO.json";
        // find POJO exist
        if (ReadFile.isFileAvailable(drlPath)) {

            TypeReference<ArrayListMultimap<String, String>> typeRef
                    = new TypeReference<ArrayListMultimap<String, String>>() {
            };
            existingVariableNameToDatatype = objectMapper.readValue(new File(drlPath.concat(POJO_NAME)), typeRef);
        } else
            existingVariableNameToDatatype = ArrayListMultimap.create();

        try {
            extractedData = extractDataFromJson(inputJson);
        } catch (ParseException e) {
            log.error(e.getClass().getSimpleName(), e);
        }

        Objects.requireNonNull(extractedData, "Input is null or wrong format.");
        // content
        String drlName = extractedData.getDrlName().concat(".drl");


        // write DRL File
        String resultContent = drlContent.initDrlContent(extractedData.getRules(), extractedData.getVariableNameToDataType());
        WriteContentToFile.write(drlPath, drlName, resultContent);


        // write POJO File
        Multimap<String, String> mergedVariableToDataType = mergeVariables(existingVariableNameToDatatype, extractedData.getVariableNameToDataType());

        try {
            String mapOfData = objectMapper.writeValueAsString(mergedVariableToDataType);
            final String path = drlPath + POJO_NAME;
            FileUtils.write(new File(path), mapOfData, "UTF-8");
        } catch (IOException e) {
            log.error(e.getClass().getSimpleName(), e);
        }
    }

    public ExtractedData extractDataFromJson(String inputJson) throws ParseException {
        ExtractedData extractedData = new ExtractedData();
        List<Rules> rulesList = new ArrayList<>();
        Multimap<String, String> variableNameToDataType = ArrayListMultimap.create();
        JSONParser parser = new JSONParser();
        JSONObject root = (JSONObject) parser.parse(inputJson);

        String projectName = ((String) root.get("project_name"));
        String loginUserEmail = (String) root.get("current_sfdc_login_user_email");
        String ruleEngineName = (String) root.get("rule_engine_name");

        extractedData.setDrlName(loginUserEmail + "_" + projectName + "_" + ruleEngineName);

        // Get rules array from root object
        JSONArray rulesArray = (JSONArray) root.get("rules");

        // Iterate all json array of rules
        for (Object rules : rulesArray) {
            Rules rule = new Rules();

            JSONObject rulesObj = (JSONObject) rules;

            rule.setRuleName(rulesObj.get("rule_name").toString());

            // get input field value and dataType
            JSONArray inputFields = (JSONArray) rulesObj.get("input_fields");
            rule.setInputFieldPair(gerVariableNameToVariableValuePair(inputFields, variableNameToDataType));

            // get output field value and dataType
            JSONArray outputFields = (JSONArray) rulesObj.get("output_fields");
            rule.setOutputFiledPair(gerVariableNameToVariableValuePair(outputFields, variableNameToDataType));

            // get variableName and data type map
            rulesList.add(rule);
        }
        extractedData.setVariableNameToDataType(variableNameToDataType);
        extractedData.setRules(rulesList);

        return extractedData;
    }

    public Multimap<String, String> gerVariableNameToVariableValuePair(JSONArray inputFieldsArray, Multimap<String, String> variableNameToDataType) {
        Multimap<String, String> variableNameToVariableValue = ArrayListMultimap.create();

        // Take all data of input fields in type and value
        for (Object obj : inputFieldsArray) {
            JSONObject singleField = (JSONObject) obj;
            Set<String> keySet = singleField.keySet();
            String variableName = getVariableName(keySet);

            // Feed data to name and datatype pair
            variableNameToDataType.put(variableName, singleField.get("type").toString());

            // feed data to name and value pair
            variableNameToVariableValue.put(variableName, singleField.get(variableName).toString());
        }
        return variableNameToVariableValue;
    }

    private String getVariableName(Set<String> keySet) {
        String variable = (String) keySet.toArray()[0];
        return variable.equals("type") ? (String) keySet.toArray()[1] : variable;
    }

    private Multimap<String, String> mergeVariables(Multimap<String, String> existingVariable, Multimap<String, String> requestedVariable) {
        Multimap<String, String> allVariable = ArrayListMultimap.create();
        allVariable.putAll(existingVariable);
        allVariable.putAll(requestedVariable);
        return allVariable;
    }

    public static void main(String[] args) {
        Map<String, String> map1 = new HashMap<>();
        map1.put("var1", "val1");
        map1.put("var2", "val2");
        map1.put("var3", "val3");
        map1.put("var4", "val4");


        Map<String, String> map2 = new HashMap<>();
        map2.put("var1", "val1");
        map2.put("var4", "val4");

        map1.putAll(map2);

        map1.forEach((k, v) -> System.out.println("Key : " + k + " Value :" + v));
    }

}
