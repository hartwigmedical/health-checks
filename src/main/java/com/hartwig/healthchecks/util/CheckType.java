package com.hartwig.healthchecks.util;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public enum CheckType {

  BOGGS(new String[]{"summary.txt", "fastqc_data.txt", "_dedup.realigned.flagstat"}) ;

  private String [] fileName;

  private CheckType(String [] fineName) {
    this.fileName = fileName;
  }

  public String [] getFileName() {
    return fileName;
  }

  public static Optional<CheckType> getByType(String type) {
    List<CheckType> types = Arrays.asList(CheckType.values());
    Optional<CheckType> returnType = types.stream()
        .filter(t -> t.toString().equalsIgnoreCase(type))
        .findFirst();

    return returnType;
  }
}
