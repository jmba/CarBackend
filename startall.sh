#!/bin/bash
cd ../boogle-maps
mvn clean package
gnome-terminal -x sh -c "java -jar target/boogle-maps-0.0.1-SNAPSHOT.jar; bash"
cd ../pricing-service
gnome-terminal -x sh -c "java -jar target/pricing-service-0.0.1-SNAPSHOT.jar; bash"
mvn clean package
cd ../vehicles-api
mvn clean package
gnome-terminal -x sh -c "java -jar target/vehicles-api-0.0.1-SNAPSHOT.jar; bash"
