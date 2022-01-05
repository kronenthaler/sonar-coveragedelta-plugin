package com.github.kronenthaler.sonarqube.plugins.coveragevariation;

import com.github.kronenthaler.sonarqube.plugins.coveragevariation.measures.CoverageVariation;
import com.github.kronenthaler.sonarqube.plugins.coveragevariation.measures.CoverageVariationMetrics;
import com.github.kronenthaler.sonarqube.plugins.coveragevariation.measures.PreviousCoverageSensor;
import org.sonar.api.CoreProperties;
import org.sonar.api.Plugin;
import org.sonar.api.Properties;
import org.sonar.api.Property;

/**
 * Plugin definition and entry point for the plugin, defined in the pom.xml
 */
@Properties(
  @Property(key = CoverageVariationPlugin.COVERAGE_VARIATION_ENABLED_KEY,
      defaultValue = "false",
      name = "coverage variation enabled",
      description = "Enable the variation calculation",
      category = CoreProperties.CATEGORY_CODE_COVERAGE)
)
public class CoverageVariationPlugin implements Plugin {
  public static final String COVERAGE_VARIATION_ENABLED_KEY = "sonar.coverage.variation.enabled";

  @Override
  public void define(Context context) {
    context
        .addExtension(CoverageVariationMetrics.class)   // Defines the metrics to be used by the Sensor and the MeasureComputer
        .addExtension(PreviousCoverageSensor.class) // Collect the project's main branch current coverage
        .addExtension(CoverageVariation.class);         // Calculates the variation of the coverage of the main branch current coverage and this scan coverage
  }
}
