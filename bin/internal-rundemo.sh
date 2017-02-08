#!/bin/bash

#set -x

DIR="$( cd "$( dirname "$0" )" && pwd )"

. ${DIR}/env.sh

DEFAULT_JVM_OPTS=-javaagent:${AGENT}/javaagent.jar
APP1_OPTS="-Dappdynamics.agent.applicationName=${APP_NAME} -Dappdynamics.agent.tierName=${TIER1_NAME} -Dappdynamics.agent.nodeName=${T1_NODE_NAME}"
APP2_OPTS="-Dappdynamics.agent.applicationName=${APP_NAME} -Dappdynamics.agent.tierName=${TIER2_NAME} -Dappdynamics.agent.nodeName=${T2_NODE_NAME}"

CONF_OPTS_BASE=-Dconfig.resource=/${AKKA_VERSION}
APP1_OPTS="${APP1_OPTS} ${CONF_OPTS_BASE}/application1.conf"
APP2_OPTS="${APP2_OPTS} ${CONF_OPTS_BASE}/application2.conf"
CMD="akka.kernel.Main com.appdynamics.akka220.appd.Boot"
LOG_FILE=${DIR}/startup.txt

rm -f ${LOG_FILE}
if [ -f ${LOG_FILE} ]
then
    echo "Could not delete ${LOG_FILE}, please manually delete and re-run this script.  Exiting."
    exit 1
else
    echo "${LOG_FILE} successfully deleted."
fi

#echo "CLASSPATH is $CLASSPATH"

# Start up App2 first.  When it reports that it has successfully started, startup App1.
# Load will start automatically.
nohup ${JAVA_HOME}/bin/java ${DEFAULT_JVM_OPTS} ${APP2_OPTS} -classpath ${CLASSPATH} ${CMD} > ${LOG_FILE} &

echo "Waiting for ${TIER2_NAME} to start..."
found=false
while [ "$found" != "true" ];
do
echo "XXX grepping..."
        grep "Successfully started Akka" ${LOG_FILE} > /dev/null 2>&1 &
        if [ $? -eq 0 ];
        then
                found="true"
                echo "XXX --------------> found!"
        else
                sleep 3
        fi
done

nohup ${JAVA_HOME}/bin/java ${DEFAULT_JVM_OPTS} ${APP1_OPTS} -classpath ${CLASSPATH} ${CMD} &

echo "Akka ping-pong demo app successfully started."

