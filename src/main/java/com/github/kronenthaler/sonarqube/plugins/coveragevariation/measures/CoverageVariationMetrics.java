package com.github.kronenthaler.sonarqube.plugins.coveragevariation.measures;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Metric;
import org.sonar.api.measures.Metrics;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Metric definitions
 */
public class CoverageVariationMetrics implements Metrics {
  /// Defines the coverage_variation metric to be used on Quality gates.
  /// By default, this metric is not visible but it can be added via the web API @c: api/qualitygates/create_condition
  public static final Metric<Double> COVERAGE_VARIATION = new Metric.Builder("coverage_variation", "Coverage variation", Metric.ValueType.PERCENT)
      .setDescription("Difference of the current coverage vs the on new code coverage.")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setHidden(false)
      .setDomain(CoreMetrics.DOMAIN_COVERAGE)
      .create();

  public static final Metric<Double> NEW_COVERAGE_VARIATION = new Metric.Builder("new_coverage_variation", "Coverage variation", Metric.ValueType.PERCENT)
      .setDescription("Difference of the current overall coverage vs the on new code overall coverage on branches and pull requests.")
      .setDirection(Metric.DIRECTION_NONE)
      .setQualitative(false)
      .setHidden(false)
      .setDomain(CoreMetrics.DOMAIN_COVERAGE)
      .create();

  /// Defines an intermediate metric previous_coverage, to be used by the CoverageVariation measure computer.
  public static final Metric<Double> PREVIOUS_COVERAGE = new Metric.Builder("previous_coverage", "Previous coverage of main branch", Metric.ValueType.PERCENT)
      .setDescription("Current project coverage percentage on the main branch.")
      .setDirection(Metric.DIRECTION_BETTER)
      .setQualitative(false)
      .setDomain(CoreMetrics.DOMAIN_COVERAGE)
      .create();

  @Override
  public List<Metric> getMetrics() {
    return asList(NEW_COVERAGE_VARIATION, COVERAGE_VARIATION, PREVIOUS_COVERAGE);
  }
}
