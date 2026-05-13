#!/bin/sh
APP_HOME="$(cd "$(dirname "$0")" && pwd)"
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"
JAVA_EXE="${JAVA_HOME:-}/bin/java"
command -v "$JAVA_EXE" > /dev/null 2>&1 || JAVA_EXE="java"
exec "$JAVA_EXE" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
