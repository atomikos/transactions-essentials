
pushd ..\..\..\..\..
call setCP.bat
echo %CLASSPATH%
popd
echo.
echo Retrieving balance of account number 90...
java -classpath "%CLASSPATH%" simple.jdbc.xadatasource.Account 90 balance
echo.
echo Withdrawing 100 from account number 90...
java -classpath "%CLASSPATH%" simple.jdbc.xadatasource.Account 90 withdraw 100
echo.
echo Retrieving new balance of account number 90...
java -classpath "%CLASSPATH%" simple.jdbc.xadatasource.Account 90 balance
echo.


pause
