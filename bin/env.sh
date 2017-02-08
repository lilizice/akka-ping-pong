#!/bin/bash

DIR="$( cd "$( dirname "$0" )" && pwd )"

MAVEN_REPO= # your local Maven repo, usually ~/.m2
AKKA_VERSION=2.3.9
AGENT= # filesystem location of your AppDynamics agent
APP_NAME="AkkaPingPongDemo"
TIER1_NAME="PingTier"
T1_NODE_NAME="PingNode"
TIER2_NAME="PongTier"
T2_NODE_NAME="PongNode"

CLASSPATH=${DIR}/../target/akka-ping-pong-1.0-SNAPSHOT.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/typesafe/config/1.3.1/config-1.3.1.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/dyuproject/protostuff/protostuff-runtime/1.0.7/protostuff-runtime-1.0.7.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/dyuproject/protostuff/protostuff-runtime-registry/1.0.7/protostuff-runtime-registry-1.0.7.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/dyuproject/protostuff/protostuff-core/1.0.7/protostuff-core-1.0.7.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/dyuproject/protostuff/protostuff-api/1.0.7/protostuff-api-1.0.7.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/dyuproject/protostuff/protostuff-collectionschema/1.0.7/protostuff-collectionschema-1.0.7.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/google/inject/guice/3.0/guice-3.0.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/google/guava/guava/13.0.1/guava-13.0.1.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/org/scala-lang/scala-library/2.11.8/scala-library-2.11.8.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/google/protobuf/protobuf-java/2.6.1/protobuf-java-2.6.1.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/javax/inject/javax.inject/1/javax.inject-1.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/aopalliance/aopalliance/1.0/aopalliance-1.0.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/asm/asm/3.1/asm-3.1.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/org/slf4j/slf4j-api/1.7.5/slf4j-api-1.7.5.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/typesafe/akka/akka-kernel_2.11/${AKKA_VERSION}/akka-kernel_2.11-${AKKA_VERSION}.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/typesafe/akka/akka-actor_2.11/${AKKA_VERSION}/akka-actor_2.11-${AKKA_VERSION}.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/typesafe/akka/akka-remote_2.11/${AKKA_VERSION}/akka-remote_2.11-${AKKA_VERSION}.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/com/typesafe/akka/akka-slf4j_2.11/${AKKA_VERSION}/akka-slf4j_2.11-${AKKA_VERSION}.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/io/netty/netty/3.6.6.Final/netty-3.6.6.Final.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/org/uncommons/maths/uncommons-maths/1.2.2a/uncommons-maths-1.2.2a.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/ch/qos/logback/logback-classic/1.0.13/logback-classic-1.0.13.jar
CLASSPATH=${CLASSPATH}:${MAVEN_REPO}/ch/qos/logback/logback-core/1.0.13/logback-core-1.0.13.jar
CLASSPATH=${CLASSPATH}:/Users/trader/dev/appd-serializer/out/artifacts/appd_serializer_jar/appd-serializer.jar
