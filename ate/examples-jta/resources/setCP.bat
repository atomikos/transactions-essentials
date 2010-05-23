echo off
if not "%JTA_HOME%" == "" goto jtaHomeSet
echo.
echo WARNING: PLEASE SET ENVIRONMENT VARIABLE JTA_HOME FOR BEST RESULTS
echo WARNING: guessing JTA_HOME - this may not work on all systems
set JTA_HOME=%cd%

:jtaHomeSet
set CLASSPATH=""
for /F "delims==" %%i in ('dir /S/B "%JTA_HOME%\..\target\lib\*.jar"') do (
	call appendToCP.bat "%%i"
)

call appendToCP.bat "%JTA_HOME%\..\target\classes"
call appendToCP.bat "."

echo
echo Using JTA_HOME=%JTA_HOME%
echo Using CLASSPATH=%CLASSPATH%
echo.

