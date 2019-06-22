package com.bizlem.drools.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WriteContentToFile {

  public static void write(String filePath, String fileName, String drlContent) {
    Path path = Paths.get(filePath+fileName);
    try (BufferedWriter writer = Files.newBufferedWriter(path)) {
      writer.write(drlContent);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
