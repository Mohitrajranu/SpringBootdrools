package com.bizlem.drools.util;

import com.bizlem.drools.model.ComparableDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.logging.log4j.util.Strings;
import org.drools.core.spi.KnowledgeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by KAI on 8/18/18.
 * Copyright 2018 by drools
 * All rights reserved.
 */
public class Helper {
    public static final Logger LOGGER = LoggerFactory.getLogger(Helper.class);

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static void log(final KnowledgeHelper drools, final String message) {
        System.out.println(message);
        System.out.println("\nrule triggered: " + drools.getRule().getName());
        System.out.println(drools.getFactHandle(drools.getMatch()));
    }


    public static LocalDate localDate(String date) {
        //convert String to LocalDate
        return LocalDate.parse(date, formatter);
    }

    public static long longFromLocalDate(String date) {
        String pureString = getStringWithoutSign(date);
        LocalDate localDate = localDate(pureString);
        return Timestamp.valueOf(localDate.atStartOfDay()).getTime();
    }

    public static String localDateFromLong(String longTimeStamp) {
        Timestamp timeStamp = new Timestamp(Long.valueOf(longTimeStamp).longValue());
        LocalDate localDate = timeStamp.toLocalDateTime().toLocalDate();
        return localDate.format(formatter);
    }

    public static String getStringWithoutSign(String variable) {

        if (StringUtils.isBlank(variable))
            throw new IllegalArgumentException("Argument must not blank.");

        if (variable.startsWith("<="))
            return StringUtils.remove(variable, "<=");
        if (variable.startsWith("<"))
            return StringUtils.remove(variable, "<");
        if (variable.startsWith(">="))
            return StringUtils.remove(variable, ">=");
        if (variable.startsWith(">"))
            return StringUtils.remove(variable, ">");
        if (variable.startsWith("=="))
            return StringUtils.remove(variable, "==");


        return variable;
    }

    public static boolean isDateFormat(String line) {
        String pattern = "[0-9]{1,2}\\/[0-9]{1,2}\\/[0-9]{4}";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(line);
        return m.matches();
    }

    public static boolean isLongTimeStampFormat(String line) {
        String pattern = "[0-9]{13}";
        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);
        // Now create matcher object.
        Matcher m = r.matcher(line);
        return m.matches() && NumberUtils.isParsable(line);
    }

    public static boolean isBetween(String line) {
        String pattern = "&&";
        // Create a Pattern object
        return line.contains(pattern);
    }


    public static String buildComparableExpression(Map<String, String> map) {
        final String expressionString = Strings.EMPTY;
        Set<Map.Entry<String, String>> entrySet = map.entrySet();
        entrySet.forEach(entry -> {
            if (entry.getKey().equalsIgnoreCase(ComparableDto.LOWER_OR_EQUALS)) {
                expressionString.concat(ComparableDto.LOWER_OR_EQUALS_SIGN);
            }
            if (entry.getValue().equalsIgnoreCase(ComparableDto.GREATER_OR_EQUALS_SIGN)) {
                if (StringUtils.isNotBlank(expressionString)) expressionString.concat(" && ");
                expressionString.concat(ComparableDto.GREATER_OR_EQUALS_SIGN);
            }
            if (entry.getValue().equalsIgnoreCase(ComparableDto.LOWER_THAN)) {
                if (StringUtils.isNotBlank(expressionString)) expressionString.concat(" && ");
                expressionString.concat(ComparableDto.LOWER_THAN);
            }
            if (entry.getValue().equalsIgnoreCase(ComparableDto.GREATER_THAN)) {
                if (StringUtils.isNotBlank(expressionString)) expressionString.concat(" && ");
                expressionString.concat(ComparableDto.GREATER_THAN);
            }
        });
        return expressionString;
    }
}
