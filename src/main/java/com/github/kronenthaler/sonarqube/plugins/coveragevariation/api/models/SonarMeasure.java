package com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.models;

public class SonarMeasure {
  public SonarMeasure.Component component;

  public static class Component {
    public String key;
    public String name;
    public String qualifier;
    public SonarMeasure.Component.Measure[] measures;

    public static class Measure {
      public String metric;
      public String value;
      public Boolean bestValue;
    }
  }
}
