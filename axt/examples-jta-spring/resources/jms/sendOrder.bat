
pushd ..\..\..
call setCP.bat
popd
echo.
java -classpath "%CLASSPATH%" -Dlog4j.logger.org.activemq=ERROR jms.OrderWithdrawal
echo.
echo "ORDER HAS BEEN SENT!"

pause
