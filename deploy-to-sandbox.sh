#!/bin/bash

set -e

JAVA_VERSION=$((java -version) 2>&1 | sed -z 's/\n/ /g')

if [[ $JAVA_VERSION =~ 'version "17.0' ]]; then
   echo "Java 17 detected"
else
  echo "Run the command in Java 17 please"
  exit 1
fi

cd prod
jbang PrepareEnvironment.java

cd ..
mvn clean package oc:build oc:resource oc:apply

