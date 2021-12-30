package com.github.kronenthaler.sonarqube.plugins.coveragedelta.tests;

import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDelta;
import com.github.kronenthaler.sonarqube.plugins.coveragedelta.measures.CoverageDeltaMetrics;
import org.junit.Test;
import org.sonar.api.ce.measure.Component;
import org.sonar.api.ce.measure.Measure;
import org.sonar.api.ce.measure.MeasureComputer;
import org.sonar.api.measures.CoreMetrics;

import static org.mockito.Mockito.*;


public class CoverageDeltaTests {

  @Test
  public void testComputeOnFile() {
    Component component = mock(Component.class);
    when(component.getType()).thenReturn(Component.Type.FILE);

    MeasureComputer.MeasureComputerContext context = mock(MeasureComputer.MeasureComputerContext.class);
    when(context.getComponent()).thenReturn(component);
    doNothing().when(context).addMeasure(isA(String.class), isA(Double.class));

    CoverageDelta target = new CoverageDelta();
    target.compute(context);

    verify(context, never()).addMeasure(anyString(), anyDouble());
  }

  @Test
  public void testComputeOnProject() {
    Component component = mock(Component.class);
    when(component.getType()).thenReturn(Component.Type.PROJECT);

    Measure previousMeasure = mock(Measure.class);
    when(previousMeasure.getDoubleValue()).thenReturn(90.0);

    Measure coverageMeasure = mock(Measure.class);
    when(coverageMeasure.getDoubleValue()).thenReturn(89.0);

    MeasureComputer.MeasureComputerContext context = mock(MeasureComputer.MeasureComputerContext.class);
    when(context.getComponent()).thenReturn(component);
    when(context.getMeasure(CoverageDeltaMetrics.PREVIOUS_COVERAGE.getKey())).thenReturn(previousMeasure);
    when(context.getMeasure(CoreMetrics.COVERAGE.getKey())).thenReturn(coverageMeasure);
    doNothing().when(context).addMeasure(isA(String.class), isA(Double.class));

    CoverageDelta target = new CoverageDelta();
    target.compute(context);

    verify(context, times(1)).addMeasure(CoverageDeltaMetrics.COVERAGE_DELTA.key(), -1.0);
  }
}
