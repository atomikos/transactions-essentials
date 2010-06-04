#!/bin/sh
cd ../../..
. "./setCP.sh"
cd -
#java -classpath "$CLASSPATH" jms.util.StartBroker 61616 >& /dev/null  &
java -classpath "$CLASSPATH" -Dlog4j.logger.org.activemq=ERROR jms.OrderWithdrawal   
echo

