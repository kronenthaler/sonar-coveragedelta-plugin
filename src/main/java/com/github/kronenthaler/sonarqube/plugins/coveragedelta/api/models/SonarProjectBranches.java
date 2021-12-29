package com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models;

public class SonarProjectBranches {
  public Branch[] branches;

  public static class Branch {
    public String name;
    public Boolean isMain;
  }
}
