package com.github.kronenthaler.sonarqube.plugins.coveragevariation.api;

import com.google.gson.Gson;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;


public class SonarServerApi {
  private static final Logger log = Loggers.get(SonarServerApi.class);

  private final URL apiUrl;

  public SonarServerApi(String host, Endpoint endpoint, Map<String, String> parameters) throws MalformedURLException, UnsupportedEncodingException {
    this(new URL(String.format("%s/%s?%s", host, endpoint.path, convertParameters(parameters))));
  }

  public SonarServerApi(URL url) {
    this.apiUrl = url;
  }

  public <T> T connect(Map<String, String> headers, Class<T> resultClass) throws IOException {
    HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
    conn.setInstanceFollowRedirects(true);
    conn.setDoInput(true);
    conn.setDoOutput(true);

    setHeaders(conn, headers);

    int statusCode = conn.getResponseCode();

    log.debug("API URL: "+ apiUrl);
    log.debug("Headers: "+ headers);
    log.debug("Status code: " + statusCode);

    if (statusCode >= 200 && statusCode < 400) {
      BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
      String jsonResponse = new String(input.readAllBytes());
      log.debug("JSON response: "+ jsonResponse);

      Gson json = new Gson();
      return json.fromJson(jsonResponse, resultClass);
    }

    return null;
  }

  private static void setHeaders(HttpURLConnection conn, Map<String, String> headers) {
    if (headers != null) {
      for (Map.Entry<String, String> header : headers.entrySet()) {
        conn.setRequestProperty(header.getKey(), header.getValue());
      }
    }
  }

  public static String convertParameters(Map<String, String> params) throws UnsupportedEncodingException {
    if (params == null) {
      return "";
    }

    StringBuilder queryString = new StringBuilder();
    String charset = Charset.defaultCharset().name();
    for (Map.Entry<String, String> param : params.entrySet()) {
      queryString
          .append(URLEncoder.encode(param.getKey(), charset))
          .append("=")
          .append(URLEncoder.encode(param.getValue(), charset))
          .append("&");
    }
    return queryString.toString();
  }

  public enum Endpoint {
    MEASURES("api/measures/component"),
    PROJECT_BRANCHES("api/project_branches/list");

    public final String path;

    Endpoint(String path) {
      this.path = path;
    }
  }
}

