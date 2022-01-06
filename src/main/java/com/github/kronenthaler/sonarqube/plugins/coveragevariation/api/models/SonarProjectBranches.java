package com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.models;

public class SonarProjectBranches {
  private Branch[] branches;

  public Branch[] getBranches() {
    return branches;
  }

  public static class Branch {
    private String name;
    private Boolean isMain;

    public String getName() {
      return name;
    }

    public Boolean isMain() {
      return isMain;
    }
  }
}
