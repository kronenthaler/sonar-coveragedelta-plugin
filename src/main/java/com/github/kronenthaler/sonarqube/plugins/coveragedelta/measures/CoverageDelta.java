package com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures;

import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.server.ServerSide;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * MeasureComputer that takes the scan's coverage and the previous main branch's coverages and creates the delta between
 * them. This metric is only calculated on project level.
 */
@ServerSide
public class CoverageDelta implements MeasureComputer {
  private static final Logger log = Loggers.get(CoverageDelta.class);

  @Override
  public MeasureComputerDefinition define(MeasureComputerDefinitionContext def) {
    return def.newDefinitionBuilder()
        .setInputMetrics(CoreMetrics.COVERAGE.getKey(), CoverageDeltaMetrics.PREVIOUS_COVERAGE.key())
        .setOutputMetrics(CoverageDeltaMetrics.COVERAGE_DELTA.key())
        .build();
  }

  @Override
  public void compute(MeasureComputerContext context) {
    // Ignore any component that is not a project.
    if (context.getComponent().getType() != Component.Type.PROJECT) {
      return;
    }

    Double scanCoverage = context.getMeasure(CoreMetrics.COVERAGE.getKey()).getDoubleValue();
    Double currentCoverage = context.getMeasure(CoverageDeltaMetrics.PREVIOUS_COVERAGE.getKey()).getDoubleValue();
    context.addMeasure(CoverageDeltaMetrics.COVERAGE_DELTA.key(), scanCoverage - currentCoverage);

    // for debug purposes
    log.info("-------------------------------------------------------------");
    log.info("Component Type: " + context.getComponent().getType());
    log.info("Component Key: " + context.getComponent().getKey());
    log.info("Scanned Coverage: " + scanCoverage);
    log.info("Current Coverage: " + currentCoverage);
    log.info("Delta Coverage: " + (scanCoverage - currentCoverage));
    log.info("-------------------------------------------------------------");
  }
}

