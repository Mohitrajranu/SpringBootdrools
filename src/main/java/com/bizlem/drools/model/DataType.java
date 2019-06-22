package com.bizlem.drools.model;

/**
 * Created by KAI on 8/30/18.
 * Copyright 2018 by drools
 * All rights reserved.
 */
public class DataType {
    private DataType() throws IllegalAccessException {throw new IllegalAccessException("Util class");}
    public static final String INTEGER = "integer";
    public static final String STRING = "string";
    public static final String DOUBLE = "double";
    public static final String FLOAT = "float";
    public static final String PERCENT = "percent";
    public static final String DATE = "date";
}
