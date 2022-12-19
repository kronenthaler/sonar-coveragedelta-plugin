package com.github.kronenthaler.sonarqube.plugins.coveragevariation.measures;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.server.ServerSide;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * MeasureComputer that takes the scan's coverage and the previous main branch's coverages and creates the variation between
 * them. This metric is only calculated on project level.
 */
@ServerSide
public class CoverageVariation implements MeasureComputer {
  private static final Logger log = Loggers.get(CoverageVariation.class);

  @Override
  public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
    return def.newDefinitionBuilder()
        .setInputMetrics(CoreMetrics.COVERAGE.getKey(), CoverageVariationMetrics.PREVIOUS_COVERAGE.key())
        .setOutputMetrics(CoverageVariationMetrics.COVERAGE_VARIATION.key(), CoverageVariationMetrics.NEW_COVERAGE_VARIATION.key())
        .build();
  }

  @Override
  public void compute(MeasureComputerContext context) {
    // Ignore any component that is not a project or the measure was not calculated (e.g. sensor disabled)
    if (context.getComponent().getType() != Component.Type.PROJECT ||
        context.getMeasure(CoverageVariationMetrics.PREVIOUS_COVERAGE.getKey()) == null) {
      return;
    }

    Measure scanCoverageMeasure = context.getMeasure(CoreMetrics.COVERAGE.getKey());
    Measure currentCoverageMeasure = context.getMeasure(CoverageVariationMetrics.PREVIOUS_COVERAGE.getKey());
    Double scanCoverage = scanCoverageMeasure != null ? scanCoverageMeasure.getDoubleValue() : 0.0;
    Double currentCoverage = currentCoverageMeasure != null ? currentCoverageMeasure.getDoubleValue() : 0.0;
    context.addMeasure(CoverageVariationMetrics.COVERAGE_VARIATION.key(), scanCoverage - currentCoverage);
    context.addMeasure(CoverageVariationMetrics.NEW_COVERAGE_VARIATION.key(), scanCoverage - currentCoverage);

    // for debug purposes
    log.info("-------------------------------------------------------------");
    log.info("Component Type: " + context.getComponent().getType());
    log.info("Component Key: " + context.getComponent().getKey());
    log.info("Scanned Coverage: " + scanCoverage);
    log.info("Current Coverage: " + currentCoverage);
    log.info("Coverage Variation: " + (scanCoverage - currentCoverage));
    log.info("-------------------------------------------------------------");
  }
}

