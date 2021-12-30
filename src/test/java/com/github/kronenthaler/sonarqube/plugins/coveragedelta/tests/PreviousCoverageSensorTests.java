package com.github.kronenthaler.sonarqube.plugins.coveragedelta.tests;

import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.SonarServerApi;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models.SonarMeasure;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models.SonarProjectBranches;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDeltaMetrics;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.PreviousCoverageSensor;
import com.google.gson.Gson;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.fs.InputComponent;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.measure.NewMeasure;
import org.sonar.api.config.Configuration;
import org.sonar.api.measures.Metric;
import org.sonar.api.scanner.fs.InputProject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PreviousCoverageSensorTests {
  static String basePath = System.getProperty("user.dir") + "/target/test-classes/";
  static String baseUrl = Paths.get(basePath).toUri().toString();

  @Test
  public void testExecuteWithErrorOnBranchRetrieval() {
    System.err.println(basePath);

    Configuration configs = mock(Configuration.class);
    when(configs.get("sonar.host.url")).thenReturn(Optional.of(baseUrl + "failed/"));
    when(configs.get(CoreProperties.PROJECT_KEY_PROPERTY)).thenReturn(Optional.of("list-project"));
    when(configs.get(CoreProperties.LOGIN)).thenReturn(Optional.of("user"));
    when(configs.get(CoreProperties.PASSWORD)).thenReturn(Optional.of("123456"));

    SensorContext context = mock(SensorContext.class);
    when(context.config()).thenReturn(configs);
    try (MockedConstruction<SonarServerApi> server = Mockito.mockConstruction(SonarServerApi.class, (mock, mockingContext) -> {
      Gson gson = new Gson();

      String branches = new String(new BufferedInputStream(new FileInputStream(basePath + "failed/" + SonarServerApi.Endpoint.PROJECT_BRANCHES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarProjectBranches.class)))
          .thenReturn(gson.fromJson(branches, SonarProjectBranches.class));

      String measure = new String(new BufferedInputStream(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarMeasure.class)))
          .thenReturn(gson.fromJson(measure, SonarMeasure.class));

    })) {
      PreviousCoverageSensor sensor = new PreviousCoverageSensor();
      sensor.execute(context);
    }
    verify(context, never()).newMeasure();
  }

  @Test
  public void testExecuteWithErrorOnCoverageRetrieval() {
    Configuration configs = mock(Configuration.class);
    when(configs.get("sonar.host.url"))
        .thenReturn(Optional.of(baseUrl + "success/"))  // pass first branch check
        .thenReturn(Optional.of(baseUrl + "failed/"));  // fail on the coverage check
    when(configs.get(CoreProperties.PROJECT_KEY_PROPERTY)).thenReturn(Optional.of("list-project"));
    when(configs.get(CoreProperties.LOGIN)).thenReturn(Optional.of("user"));
    when(configs.get(CoreProperties.PASSWORD)).thenReturn(Optional.of("123456"));

    SensorContext context = mock(SensorContext.class);
    when(context.config()).thenReturn(configs);

    try (MockedConstruction<SonarServerApi> server = Mockito.mockConstruction(SonarServerApi.class, (mock, mockingContext) -> {
      Gson gson = new Gson();

      String branches = new String(new BufferedInputStream(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.PROJECT_BRANCHES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarProjectBranches.class)))
          .thenReturn(gson.fromJson(branches, SonarProjectBranches.class));

      String measure = new String(new BufferedInputStream(new FileInputStream(basePath + "failed/" + SonarServerApi.Endpoint.MEASURES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarMeasure.class)))
          .thenReturn(gson.fromJson(measure, SonarMeasure.class));

    })) {
      PreviousCoverageSensor sensor = new PreviousCoverageSensor();
      sensor.execute(context);
    }
    verify(context, never()).newMeasure();
  }

  @Test
  public void testExecuteSuccessCreateMetric() throws Exception {
    Configuration configs = mock(Configuration.class);
    when(configs.get("sonar.host.url")).thenReturn(Optional.of(baseUrl + "success/"));
    when(configs.get(CoreProperties.PROJECT_KEY_PROPERTY)).thenReturn(Optional.of("list-project"));
    when(configs.get(CoreProperties.LOGIN)).thenReturn(Optional.of("user"));
    when(configs.get(CoreProperties.PASSWORD)).thenReturn(Optional.of("123456"));

    InputProject project = mock(InputProject.class);

    ArgumentCaptor<Metric> metricCapture = ArgumentCaptor.forClass(Metric.class);
    ArgumentCaptor<Double> valueCapture = ArgumentCaptor.forClass(Double.class);
    ArgumentCaptor<InputComponent> componentCapture = ArgumentCaptor.forClass(InputComponent.class);

    NewMeasure newMeasure = mock(NewMeasure.class);

    SensorContext context = mock(SensorContext.class, RETURNS_DEEP_STUBS);
    when(context.config()).thenReturn(configs);
    when(context.project()).thenReturn(project);
    when(context.newMeasure()
        .forMetric(metricCapture.capture())
        .on(componentCapture.capture())
        .withValue(valueCapture.capture())).thenReturn(newMeasure);
    doNothing().when(newMeasure).save();

    try (MockedConstruction<SonarServerApi> server = Mockito.mockConstruction(SonarServerApi.class, (mock, mockingContext) -> {
      Gson gson = new Gson();

      String branches = new String(new BufferedInputStream(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.PROJECT_BRANCHES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarProjectBranches.class)))
          .thenReturn(gson.fromJson(branches, SonarProjectBranches.class));

      String measure = new String(new BufferedInputStream(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path)).readAllBytes());
      when(mock.connect(any(), any(), eq(SonarMeasure.class)))
          .thenReturn(gson.fromJson(measure, SonarMeasure.class));

    })) {
      PreviousCoverageSensor sensor = new PreviousCoverageSensor();
      sensor.execute(context);
    }

    verify(newMeasure, times(1)).save();
    assertEquals(metricCapture.getValue(), CoverageDeltaMetrics.PREVIOUS_COVERAGE);
    assertEquals(componentCapture.getValue(), project);
    assertEquals(valueCapture.getValue(), (Double) 38.5);
  }
}
