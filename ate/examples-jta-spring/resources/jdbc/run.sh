#!/bin/sh
cd ../../..
. "./setCP.sh"
cd -
java -classpath "$CLASSPATH" -Dcom.atomikos.icatch.file=jta.properties jdbc.Main  
echo

