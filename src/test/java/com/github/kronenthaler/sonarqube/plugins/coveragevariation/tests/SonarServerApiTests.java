package com.github.kronenthaler.sonarqube.plugins.coveragevariation.tests;

import com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.SonarServerApi;
import com.github.kronenthaler.sonarqube.plugins.coveragevariation.api.models.SonarMeasure;
import org.hamcrest.core.StringContains;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
    URL mockedUrl = mock(URL.class);
    when(mockedUrl.toString()).thenReturn(baseUrl + "/" + SonarServerApi.Endpoint.MEASURES.path);

    HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
    when(mockedHttp.getResponseCode()).thenReturn(400);

    when(mockedUrl.openConnection()).thenReturn(mockedHttp);

    SonarServerApi api = new SonarServerApi(mockedUrl);
    SonarMeasure measure = api.connect(null, SonarMeasure.class);

    assertNull(measure);
  }

  @Test
  public void testConnectFailure() throws IOException {
    URL mockedUrl = mock(URL.class);
    when(mockedUrl.toString()).thenReturn(baseUrl + "/" + SonarServerApi.Endpoint.MEASURES.path);

    HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
    when(mockedHttp.getResponseCode()).thenReturn(200);
    when(mockedHttp.getInputStream()).thenReturn(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path));

    when(mockedUrl.openConnection()).thenReturn(mockedHttp);

    SonarServerApi api = new SonarServerApi(mockedUrl);
    SonarMeasure measure = api.connect(null, SonarMeasure.class);

    assertNotNull(measure);
  }

  @Test
  public void testRequestWithHeaders() throws IOException {
    ArgumentCaptor<String> keyCapture = ArgumentCaptor.forClass(String.class);
    ArgumentCaptor<String> valueCapture = ArgumentCaptor.forClass(String.class);

    HttpURLConnection mockedHttp = mock(HttpURLConnection.class);
    when(mockedHttp.getResponseCode()).thenReturn(200);
    when(mockedHttp.getInputStream()).thenReturn(new FileInputStream(basePath + "success/" + SonarServerApi.Endpoint.MEASURES.path));

    doNothing().when(mockedHttp).setRequestProperty(keyCapture.capture(), valueCapture.capture());

    URL mockedUrl = mock(URL.class);
    when(mockedUrl.toString()).thenReturn(baseUrl + "/" + SonarServerApi.Endpoint.MEASURES.path);

    when(mockedHttp.getURL()).thenReturn(mockedUrl);
    when(mockedUrl.openConnection()).thenReturn(mockedHttp);

    SonarServerApi api = new SonarServerApi(mockedUrl);

    HashMap<String, String> headers = new HashMap<>();
    headers.put("Authorization", "Basic cmljay1yb2xsOg==");

    SonarMeasure measure = api.connect(headers, SonarMeasure.class);

    // check headers
    assertNotNull(measure);
    assertEquals(keyCapture.getValue(), "Authorization");
    assertEquals(valueCapture.getValue(), "Basic cmljay1yb2xsOg==");
  }

  @Test
  public void testConvertParameters() throws UnsupportedEncodingException {
    HashMap<String, String> params = new HashMap<>();
    params.put("component", "composed:component-key");
    params.put("metric", "coverage");
    params.put("branch", "feature/branch/with-escapable-chars");

    String queryString = SonarServerApi.convertParameters(params);
    assertThat(queryString, StringContains.containsString("component=composed%3Acomponent-key"));
    assertThat(queryString, StringContains.containsString("metric=coverage"));
    assertThat(queryString, StringContains.containsString("branch=feature%2Fbranch%2Fwith-escapable-chars"));
  }

  @Test
  public void testConvertEmptyParameters() throws UnsupportedEncodingException {
    assertEquals("", SonarServerApi.convertParameters(null));
  }
}
