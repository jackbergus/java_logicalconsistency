#!/bin/bash
export MAVEN_OPTS="-Xmx60g -Dfile.encoding=UTF-8"
mvn compile exec:java -Dexec.mainClass="org.ufl.hypogator.jackb.main.JsonServer"
