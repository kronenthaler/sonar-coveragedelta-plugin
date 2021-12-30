/*
 * Example Plugin for SonarQube
 * Copyright (C) 2009-2020 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures;

import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models.SonarMeasure;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models.SonarProjectBranches;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.SonarServerApi;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.config.Configuration;
import org.sonar.api.scanner.ScannerSide;
import org.sonar.api.scanner.sensor.ProjectSensor;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.NoSuchElementException;

import static com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDeltaMetrics.PREVIOUS_COVERAGE;

/**
 * Scanner feeds raw measures on files but must not aggregate values to directories and project.
 * This class emulates loading of file measures from a 3rd-party analyser.
 */
@SuppressWarnings("Since15")
@ScannerSide
public class PreviousCoverageSensor implements ProjectSensor {
  static private final Logger log = Loggers.get(PreviousCoverageSensor.class);

  private static SonarServerApi initialize(SensorContext context, SonarServerApi.Endpoint endpoint) {
    return new SonarServerApi(context.config().get("sonar.host.url").orElseThrow(), endpoint);
  }

  private static String getAuthorizationHeader(Configuration configs) {
    String payload = configs.get(CoreProperties.LOGIN).orElseThrow() + ":" + configs.get(CoreProperties.PASSWORD).orElse("");
    return "Basic " + Base64.getEncoder().encodeToString(payload.getBytes());
  }

  @Override
  public void describe(SensorDescriptor descriptor) {
    descriptor.name("Coverage delta sensor");
  }

  @Override
  public void execute(SensorContext context) {
    try {
      Double defaultBranchCoverage = currentCoverage(context);
      log.info("Default Branch coverage: " + defaultBranchCoverage);

      context.<Double>newMeasure()
          .forMetric(PREVIOUS_COVERAGE)
          .on(context.project())
          .withValue(defaultBranchCoverage)
          .save();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private Double currentCoverage(SensorContext context) throws Exception {
    String branch = defaultBranch(context);
    log.info("Main Branch: " + branch);

    Configuration configs = context.config();
    String componentKey = configs.get(org.sonar.api.CoreProperties.PROJECT_KEY_PROPERTY).orElseThrow();

    try {
      SonarServerApi api = initialize(context, SonarServerApi.Endpoint.MEASURES);
      HashMap<String, String> params = new HashMap<>();
      params.put("component", componentKey);
      params.put("metricKeys", "coverage");
      params.put("branch", branch);

      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", getAuthorizationHeader(configs));

      SonarMeasure measure = api.connect(params, headers, SonarMeasure.class);
      return Double.parseDouble(measure.component.measures[0].value);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return 0.0;
  }

  private String defaultBranch(SensorContext context) throws NoSuchElementException {
    Configuration configs = context.config();
    String componentKey = configs.get(CoreProperties.PROJECT_KEY_PROPERTY).orElseThrow();

    try {
      SonarServerApi api = initialize(context, SonarServerApi.Endpoint.PROJECT_BRANCHES);
      HashMap<String, String> params = new HashMap<>();
      params.put("project", componentKey);

      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", getAuthorizationHeader(configs));

      SonarProjectBranches branches = api.connect(params, headers, SonarProjectBranches.class);

      return Arrays.stream(branches.branches)
          .filter(b -> b.isMain)
          .findFirst().orElseThrow()
          .name;
    } catch (IOException e) {
      e.printStackTrace();
      return "";
    }
  }
}
