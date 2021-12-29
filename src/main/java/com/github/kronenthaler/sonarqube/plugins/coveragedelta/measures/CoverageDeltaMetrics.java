package com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures;

import java.util.List;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import static java.util.Arrays.asList;

/**
 * Metric definitions
 */
public class CoverageDeltaMetrics implements Metrics {
  /// Defines the coverage_delta metric to be used on Quality gates.
  /// By default, this metric is not visible but it can be added via the web API @c: api/qualitygates/create_condition
  public static final Metric<Double> COVERAGE_DELTA = new Metric.Builder("coverage_delta", "Coverage delta", Metric.ValueType.PERCENT)
    .setDescription("Difference of the current coverage vs the on new code coverage.")
    .setDirection(Metric.DIRECTION_NONE)
    .setQualitative(false)
    .setDomain(CoreMetrics.DOMAIN_COVERAGE)
    .create();

  /// Defines an intermediate metric previous_coverage, to be used by the CoverageDelta measure computer.
  public static final Metric<Double> PREVIOUS_COVERAGE = new Metric.Builder("previous_coverage", "Previous coverage of main branch", Metric.ValueType.PERCENT)
        .setDescription("Current project coverage percentage on the main branch.")
        .setDirection(Metric.DIRECTION_BETTER)
        .setQualitative(false)
        .setDomain(CoreMetrics.DOMAIN_COVERAGE)
        .create();

  @Override
  public List<Metric> getMetrics() {
    return asList(COVERAGE_DELTA, PREVIOUS_COVERAGE);
  }
}
