#!/bin/bash
mvn clean compile
mvn exec:java -D"exec.mainClass"="org.ufl.aida.ldc.dbloader.Main"