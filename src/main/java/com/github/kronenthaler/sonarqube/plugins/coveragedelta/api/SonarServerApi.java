package com.github.kronenthaler.sonarqube.plugins.coveragedelta.api;

import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;


public class SonarServerApi {
  private final String host;
  private final Endpoint endpoint;

  public SonarServerApi(String host, Endpoint endpoint) {
    this.host = host;
    this.endpoint = endpoint;
  }

  public <Result> Result connect(Map<String, String> parameters, Map<String, String> headers, Class<Result> resultClass) throws IOException {
    URL apiUrl = new URL(String.format("%s/%s?%s", this.host, this.endpoint.path, convertParameters(parameters)));

    HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
    conn.setInstanceFollowRedirects(true);
    conn.setDoInput(true);
    conn.setDoOutput(true);

    for (Map.Entry<String, String> header : headers.entrySet()) {
      conn.setRequestProperty(header.getKey(), header.getValue());
    }

    int statusCode = conn.getResponseCode();
    if (statusCode >= 200 && statusCode < 400) {
      BufferedInputStream input = new BufferedInputStream(conn.getInputStream());
      String jsonResponse = new String(input.readAllBytes());

      Gson json = new Gson();
      return json.fromJson(jsonResponse, resultClass);
    }

    return null;
  }

  private String convertParameters(Map<String, String> params) throws UnsupportedEncodingException {
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

