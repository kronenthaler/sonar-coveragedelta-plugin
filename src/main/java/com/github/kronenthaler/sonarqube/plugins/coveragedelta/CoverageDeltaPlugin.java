package com.github.kronenthaler.sonarqube.plugins.coveragedelta;

import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDelta;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDeltaMetrics;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.PreviousCoverageSensor;
import org.sonar.api.Plugin;

/**
 * Plugin definition and entry point for the plugin, defined in the pom.xml
 */
public class CoverageDeltaPlugin implements Plugin {

  @Override
  public void define(Context context) {
    context
        .addExtension(CoverageDeltaMetrics.class)   // Defines the metrics to be used by the Sensor and the MeasureComputer
        .addExtension(PreviousCoverageSensor.class) // Collect the project's main branch current coverage
        .addExtension(CoverageDelta.class);         // Calculates the delta of the coverage of the main branch current coverage and this scan coverage
  }
}
