#!/bin/sh
cd ../../../..
. "./setCP.sh"
cd -
java -classpath "$CLASSPATH" jms.util.StartBroker 61616 >& /dev/null  &
echo
java -classpath "$CLASSPATH" -Dlog4j.logger.org.activemq=ERROR -Dcom.atomikos.icatch.file=sender.jta.properties  jms.sender.TestSender 
echo


