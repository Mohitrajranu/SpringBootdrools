package com.bizlem.drools.service.impl;

import com.bizlem.drools.model.Rules;
import com.bizlem.drools.service.GenerateDRLContent;
import com.bizlem.drools.util.Helper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.bizlem.drools.model.DataType.*;
import static com.bizlem.drools.util.Helper.LOGGER;
import static com.bizlem.drools.util.Helper.getStringWithoutSign;

@Service
public class GenerateDRLContentImpl implements GenerateDRLContent {
    private static final String MAP_GET = "$map.get(\"";
    private final String newLine = System.lineSeparator();

    @Override
    public String initDrlContent(List<Rules> ruleList, Multimap<String, String> variableToDataType) throws JsonProcessingException {

        StringBuilder buffer = new StringBuilder();
        // Add package structure
        buffer.append(packageAndImportClasses());

        for (Rules rules : ruleList) {
            buffer.append(newLine);
            buffer.append("rule ").append(rules.getRuleName()).append(newLine);
            buffer.append("dialect \"mvel\"");
            buffer.append(newLine).append("when").append(newLine);
            buffer.append("$map: Map (");

            // add condition for each rule
            buffer.append(condition(rules.getInputFieldPair(), variableToDataType));
            buffer.append(")").append(newLine);

            buffer.append("then");
            buffer.append(newLine);

            // add action for each rule
            buffer.append(action(rules.getOutputFiledPair()));

            // add end of the rule
            buffer.append("end");
            buffer.append(newLine);
        }
        return buffer.toString();
    }

    private String packageAndImportClasses() {

        StringBuffer buffer = new StringBuffer();
        buffer.append("package com.bizlem;");
        buffer.append(newLine);
        buffer.append(newLine);
        buffer.append("import java.util.Map;");
        buffer.append(newLine);
        return buffer.toString();
    }

    private String condition(Multimap<String, String> ruleConditionFields, Multimap<String, String> variableNameToDataType) throws JsonProcessingException {
        List<String> conditions = new ArrayList<String>();
        for (Map.Entry<String, String> entry : ruleConditionFields.entries()) {
            String variableName = entry.getKey();
            String variableValue = entry.getValue();
            Collection<String> dataType = variableNameToDataType.get(entry.getKey());

            conditions.add(valueOnDataType(variableName, variableValue, dataType));
        }

        ObjectMapper objectMapper = new ObjectMapper();
        LOGGER.info("The condition list : {}", objectMapper.writeValueAsString(conditions));
        conditions = conditions.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        return StringUtils.join(conditions, " && ");
    }

    private String valueOnDataType(String variableName, String variableValue, Collection<String> dataType) {
        StringBuilder buffer = new StringBuilder();


        switch (StringUtils.trimToEmpty(dataType.toArray()[0].toString()).toLowerCase()) {
            case STRING:
                buffer.append(MAP_GET).append(variableName).append("\")").append(" ");
                buffer.append(checkOperationString(variableValue));
                break;

            case INTEGER:
            case DOUBLE:
            case FLOAT:
                buffer.append(MAP_GET).append(variableName).append("\")").append(" ");
                buffer.append(checkOperatorInteger(variableValue)).append(" ");
                break;

            case DATE:
                if (StringUtils.isBlank(variableValue)) break;
                buffer.append(MAP_GET).append(variableName).append("\")").append(" ");
                buffer.append(checkDateOperation(variableValue)).append(" ");
                break;

            case PERCENT:
                if (StringUtils.isBlank(variableValue)) break;
                buffer.append(MAP_GET).append(variableName).append("\")").append(" ");
                buffer.append(checkPercentOperation(variableValue)).append(" ");
                break;

            default:
                buffer.append(MAP_GET).append(variableName).append("\")").append(" == ");
                buffer.append(variableValue);
        }
        return buffer.toString();
    }

    private String checkOperationString(String variable){
        if(variable.startsWith("!=")){
            return "!=" + "\"" + StringUtils.remove(variable, "!=") + "\"";
        }else {
            return " in (" + splitValue(variable) + ") ";
        }
    }

    private String splitValue(String variableValue) {
        return Arrays.stream(variableValue.split(",")).map(s -> "\"" + s + "\"").collect(Collectors.joining(","));
    }

    private String action(Multimap<String, String> ruleActionFields) {
        StringBuilder buffer = new StringBuilder();
        for (Map.Entry<String, String> entry : ruleActionFields.entries()) {

            String variableName = entry.getKey();
            String variableValue = entry.getValue();

            buffer.append("$map.put(\"").append(variableName).append("\"").append(",\"").append(variableValue).append("\"").append(");");
            buffer.append(newLine);
        }
        return buffer.toString();
    }

    private String checkOperatorInteger(String variable) {
        if (variable.startsWith("<") || variable.startsWith(">") || variable.startsWith("==") || variable.startsWith(">=") || variable.startsWith("<=") || variable.startsWith("!="))
            return variable;

        return " == " + variable;
    }

    private String checkPercentOperation(String variableValue) {
        String variable = StringUtils.chop(variableValue);
        String result = getStringWithoutSign(variable);
        LOGGER.info("text of percentage {} is {}", variableValue, result);
        if (variable.startsWith("==") || variable.startsWith(">=") || variable.startsWith("<="))
            return variable.substring(0, 2) + result;
        if (variable.startsWith("<") || variable.startsWith(">"))
            return variable.substring(0, 1) + result;

        return " == " + result;
    }


    private String checkDateOperation(String variable) {
        LOGGER.info("Local date string without sign is {}", getStringWithoutSign(variable));
        long longLocalDate = Helper.longFromLocalDate(variable);
        LOGGER.info("longLocalDate is {} is extracted from {}", longLocalDate, variable);
        if (variable.startsWith("==") || variable.startsWith(">=") || variable.startsWith("<="))
            return variable.substring(0, 2) + longLocalDate;
        if (variable.startsWith("<") || variable.startsWith(">")) {
            return variable.substring(0, 1) + longLocalDate;

        }
        return " == " + longLocalDate;
    }


}
