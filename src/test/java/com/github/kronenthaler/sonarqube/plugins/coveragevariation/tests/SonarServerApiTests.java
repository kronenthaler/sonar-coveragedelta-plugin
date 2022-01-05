package com.github.kronenthaler.sonarqube.plugins.coveragevariation.tests;

import com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.SonarServerApi;
import com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.models.*;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;

import static junit.framework.TestCase.*;
import static org.hamcrest.MatcherAssert.assertThat;
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
      SonarMeasure measure = api.connect(null, null, SonarMeasure.class);

      assertNull(measure);
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
      SonarMeasure measure = api.connect(null, null, SonarMeasure.class);

      assertNotNull(measure);
    }
  }

  @Test
  public void testRequestWithHeaders() throws IOException {
    ArgumentCaptor<String> keyCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);

    HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
    when(mockedHttp.getResponseCode()).thenReturn(200);
    when(mockedHttp.getInputStream()).thenReturn(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path));

    doNothing().when(mockedHttp).setRequestProperty(keyCapture.capture(), valueCapture.capture());


    try (MockedConstruction<URL> url = Mockito.mockConstruction(URL.class, (mock, mockingContext) -> {
      if (mockingContext.arguments().get(0) instanceof String) {
        String urlString = (String) mockingContext.arguments().get(0);
        when(mock.toString()).thenReturn(urlString);
      }

      when(mockedHttp.getURL()).thenReturn(mock);
      when(mock.openConnection()).thenReturn(mockedHttp);
    })){
      SonarServerApi api = new SonarServerApi(baseUrl, SonarServerApi.Endpoint.MEASURES);
      HashMap<String, String> params = new HashMap<>();
      params.put("component", "component-key");
      params.put("metric", "coverage");
      params.put("branch", "feature/branch/with-escapable-chars");

      HashMap<String, String> headers = new HashMap<>();
      headers.put("Authorization", "Basic cmljay1yb2xsOg==");

      SonarMeasure measure = api.connect(params, headers, SonarMeasure.class);

      String urlString = mockedHttp.getURL().toString();

      // check headers
      assertNotNull(measure);
      assertEquals(keyCapture.getValue(), "Authorization");
      assertEquals(valueCapture.getValue(), "Basic cmljay1yb2xsOg==");

      // check query string parameters
      assertThat(urlString, StringContains.containsString("component=component-key"));
      assertThat(urlString, StringContains.containsString("metric=coverage"));
      assertThat(urlString, StringContains.containsString("branch=feature%2Fbranch%2Fwith-escapable-chars"));
    }
  }
}
