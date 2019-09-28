#!/usr/bin/env bash

# This script is for manual native images, prefer running ./gradlew nativeImage over running this script

export JAR="jira-cli.jar"

cd graal

echo "Unpacking $JAR"

if [ -d unpack ]; then rm -Rf unpack; fi
mkdir unpack

cd unpack
if [ -f ../../$JAR ]; then
  echo "Getting $JAR from root"
  jar -xvf ../../$JAR >/dev/null 2>&1
elif [ -f ../../build/libs/$JAR ]; then
  echo "Getting $JAR from build"
  jar -xvf ../../build/libs/$JAR >/dev/null 2>&1
else
  echo "Jar $JAR not found!"
  exit 1
fi

export CP=.:$LIBPATH

#LIBSUNEC_SO_PATH=$(find -L $JAVA_HOME -name libsunec.so)
#if [[ ! -f $LIBSUNEC_SO_PATH ]] ; then
#    echo 'Failed to find libsunec.so library in $JAVA_HOME - this library is mandatory for building native image'
#    exit 1
#fi
#
#echo "Copying $LIBSUNEC_SO_PATH to ."
#cp $LIBSUNEC_SO_PATH ./

echo "Compile"
native-image \
  -Dio.netty.noUnsafe=true \
  --no-server \
  --enable-https \
  --enable-all-security-services \
  -J-Djava.security.properties=../java.security.overrides \
  -H:Name=jira-cli \
  -H:EnableURLProtocols=https \
  -H:+ReportExceptionStackTraces \
  -H:ReflectionConfigurationFiles=META-INF/native-image/picocli-generated/jiracli/jira-cli/reflect-config.json,../extra-reflect.json \
  --no-fallback \
  --allow-incomplete-classpath \
  --report-unsupported-elements-at-runtime \
  --initialize-at-run-time=org.apache.http.Header,org.apache.http.params.HttpParams,org.apache.http.StatusLine,org.apache.http.ProtocolVerision,org.apache.http.client.methods.Closeable,org.apache.http.client.methods.CloseableHttpResponse,org.apache.http.ProtocolVersion,org.apache.http.HttpEntity,io.atlassian.fugue.Suppliers \
  -DremoveUnusedAutoconfig=true \
  -cp $CP jiracli.CheckSum

cp jira-cli ../../