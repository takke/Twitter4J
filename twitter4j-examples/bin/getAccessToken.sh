#!/bin/sh
. ./setEnv.sh

echo $JAVA_HOME/bin/java $MEM_ARGS -cp $CLASSPATH twitter4a.examples.oauth.GetAccessToken "$@"
$JAVA_HOME/bin/java $MEM_ARGS -cp $CLASSPATH twitter4a.examples.oauth.GetAccessToken "$@"