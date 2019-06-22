package com.bizlem.drools.util;

import java.io.File;

public class ReadFile {
    private static final String FILE_NAME = "VariablePOJO.json";
    public static boolean isFileAvailable(String filePath) {
        File f = new File(filePath.concat(FILE_NAME));
        return f.exists() && !f.isDirectory();
    }
}
