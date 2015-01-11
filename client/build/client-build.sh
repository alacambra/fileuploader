#!/bin/sh
echo -n "http://coses.lacambra.de/uploader/r/file/" > "../src/main/resources/endpoint"
cd ..
mvn clean && mvn package