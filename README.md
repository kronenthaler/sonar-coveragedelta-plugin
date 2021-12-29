# Sonar Coverage Delta Plugin
A Sonarqube plugin to calculate the delta coverage between the current scan and the previous one.

## Installation

Copy the release jar into the sonar's `extensions/plugins` folder.

## Quality Gate configuration

Define a condition for the Quality Gate that you want to monitor. This cannot be done via the Web UI because the custom metrics are not displayed. 

However, it's possible to create the condition using the web APIs. 

```shell
curl -X POST -u "$SONAR_TOKEN:" "$SONAR_HOST_URL/api/qualitygates/create_condition?error=<error-threshold>&gateName=<qg-name>&metric=coverage_delta&op=LT"
```

Where:
* `SONAR_TOKEN`: is an access token of a user with admin permissions
* `SONAR_HOST_URL`: the url to the sonar instance to configure the quality gate condition
* `error-threshold`: the maximum difference to allow on new code. Ideally, 0% decrease. But it can be set to -1% or -2% for some leniancy.
* `qg-name`: the Quality Gate to add the condition to.
* `coverage_delta`: this is the new metric we want to add. Do not change this.
* `LT`: the direction we want to validate the change of delta. LT means less than `<error-threshold>` will fail the Quality Gate.

## Usage

Scan as normal!




