
pushd ..\..\..
call setCP.bat
popd
echo.
start /B java -classpath "%CLASSPATH%" -Dlog4j.logger.org.activemq=ERROR jms.util.StartBroker 61616 
echo.
java -classpath "%CLASSPATH%" -Dlog4j.logger.org.activemq=ERROR -Dcom.atomikos.icatch.file=jta.properties jms.StartBank

echo.

pause

