# Triple Pattern Fragments <img src="http://linkeddatafragments.org/images/logo.svg" width="100" align="right" alt="" />
Triple Pattern Fragments server in Java.

## Build
Execute the following command to create a WAR file:
```
$ mvn install
```

## Deploy
Use an application server such as [Tomcat](http://tomcat.apache.org/) to deploy the WAR file.

Place an `ldf-server.json` configuration file with the data sources (analogous to the example file) in the `../conf` folder relative to the deployed WAR file.

## Status
This is software is still under development. It currently only supports:
- HDT data sources
- Turtle output

A [more complete server](https://github.com/LinkedDataFragments/Server.js/) has been implemented for the Node.js platform.
