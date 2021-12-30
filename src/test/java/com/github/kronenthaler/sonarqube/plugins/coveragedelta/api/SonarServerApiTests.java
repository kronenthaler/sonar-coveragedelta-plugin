package com.github.kronenthaler.sonarqube.plugins.coveragedelta.api;

import com.github.kronenthaler.sonarqube.plugins.coveragedelta.api.models.*;
import junit.framework.TestCase;
import org.junit.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;

import static org.mockito.Mockito.*;

public class SonarServerApiTests {
  static String basePath = System.getProperty("user.dir") + "/target/test-classes/";
  static String baseUrl = Paths.get(basePath).toUri().toString();


  @Test
  public void testConnectSuccessfully() throws IOException {
    try (MockedConstruction<URL> url = Mockito.mockConstruction(URL.class, (mock, mockingContext) -> {
      HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
      when(mockedHttp.getResponseCode()).thenReturn(400);

      when(mock.openConnection()).thenReturn(mockedHttp);
    })){
      SonarServerApi api = new SonarServerApi(baseUrl, SonarServerApi.Endpoint.MEASURES);
      SonarMeasure measure = api.connect(new HashMap<>(), new HashMap<>(), SonarMeasure.class);

      TestCase.assertNull(measure);
    }
  }

  @Test
  public void testConnectFailure() throws IOException {
    try (MockedConstruction<URL> url = Mockito.mockConstruction(URL.class, (mock, mockingContext) -> {
      HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
      when(mockedHttp.getResponseCode()).thenReturn(200);
      when(mockedHttp.getInputStream()).thenReturn(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path));

      when(mock.openConnection()).thenReturn(mockedHttp);
    })){
      SonarServerApi api = new SonarServerApi(baseUrl, SonarServerApi.Endpoint.MEASURES);
      SonarMeasure measure = api.connect(new HashMap<>(), new HashMap<>(), SonarMeasure.class);

      TestCase.assertNotNull(measure);
    }
  }
}
