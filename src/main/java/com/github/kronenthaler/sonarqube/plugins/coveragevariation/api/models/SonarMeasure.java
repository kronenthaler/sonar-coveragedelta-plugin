package com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.models;

public class SonarMeasure {
  private SonarMeasure.Component component;

  public SonarMeasure.Component getComponent() {
    return component;
  }

  public static class Component {
    private SonarMeasure.Component.Measure[] measures;

    public SonarMeasure.Component.Measure[] getMeasures() {
      return measures;
    }

    public static class Measure {
      private String value;

      public String getValue() {
        return value;
      }
    }
  }
}
