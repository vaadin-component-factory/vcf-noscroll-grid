# NoScrollGrid Component Demo

Demo web application for noscroll-grid.


## Development instructions

To build JAR package, run following command:
```
mvn clean install
```

To build JAR with production mode enabled:
```
mvn clean package -Pproduction
```

Starting the demo server:
```
mvn jetty:run
```

This deploys demo at http://localhost:8080


Starting the demo in production mode:
```
mvn jetty:run-exploded -Dvaadin.productionMode=true
```