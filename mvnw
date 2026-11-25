#!/bin/sh
export JAVA_HOME="${JAVA_HOME:-C:/Program Files/Java/jdk-21.0.11}"
MAVEN_HOME="C:/Users/DELL/apache-maven-3.9.6"
exec "$MAVEN_HOME/bin/mvn" "$@"
