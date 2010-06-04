#!/bin/sh
cd ../../../..
. "./setCP.sh"
cd -
echo
echo Retrieving balance of account number 90...
java -classpath "$CLASSPATH" advanced.xa.XaAccount 90 balance
echo
echo Withdrawing 100 from account number 90...
java -classpath "$CLASSPATH" advanced.xa.XaAccount 90 withdraw 100
echo
echo Retrieving new balance of account number 90...
java -classpath "$CLASSPATH" advanced.xa.XaAccount 90 balance
echo


