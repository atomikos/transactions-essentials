
pushd ..\..\..
call setCP.bat
popd
echo.
java -classpath "%CLASSPATH%" -Dcom.atomikos.icatch.file=jta.properties jdbc.Main  
echo.

pause

