[![Build](https://github.com/kronenthaler/sonar-coveragedelta-plugin/actions/workflows/build.yml/badge.svg)](https://github.com/kronenthaler/sonar-coveragedelta-plugin/actions/workflows/build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=kronenthaler_sonar-coveragedelta-plugin&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=kronenthaler_sonar-coveragedelta-plugin)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=kronenthaler_sonar-coveragedelta-plugin&metric=coverage)](https://sonarcloud.io/summary/new_code?id=kronenthaler_sonar-coveragedelta-plugin)

# Sonar Coverage Variation Plugin
A Sonarqube plugin to calculate the variation coverage between the current scan and the previous one.

## Requirements

* Sonarqube 9.0.1+
* Java 11

## Installation

Copy the release jar into the sonar's `extensions/plugins` folder.

## Quality Gate configuration

Define a condition for the Quality Gate that you want to monitor. This cannot be done via the Web UI because the custom metrics are not displayed. 

However, it's possible to create the condition using the web APIs. 

```shell
curl -X POST -u "$SONAR_TOKEN:" "$SONAR_HOST_URL/api/qualitygates/create_condition?error=<error-threshold>&gateName=<qg-name>&metric=coverage_variation&op=LT"
```

Where:
* `SONAR_TOKEN`: is an access token of a user with admin permissions
* `SONAR_HOST_URL`: the url to the sonar instance to configure the quality gate condition
* `error-threshold`: the maximum percentual difference to allow on new code. Ideally, 0 (per-cent) decrease. But it can be set to -1 or -2 for some leniency.
* `qg-name`: the Quality Gate to add the condition to.
* `coverage_variation`: this is the new metric we want to add. Do not change this.
* `LT`: the direction we want to validate the change of variation. LT means less than `<error-threshold>` will fail the Quality Gate.

## Usage

For safety, the variation scan is disabled by default. To enable it, add the following property to your scanner command:

```shell
sonar-scanner -Dsonar.coverage.variation.enabled=true ...
```
