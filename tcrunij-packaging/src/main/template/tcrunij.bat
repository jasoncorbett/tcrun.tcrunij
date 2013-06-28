@echo off
rem This magic code found on http://stackoverflow.com/questions/524081/bat-file-to-create-java-classpath
setLocal EnableDelayedExpansion

rem We copy the files to a temp directory in the users home directory to handle long paths on Windows
echo "Loading jar files....." 
mkdir %HOMEPATH%\temptcrunij >nul 2>&1
xcopy /S /E /Y * %HOMEPATH%\temptcrunij\ >nul 2>&1

 set CLASSPATH="
 for /R %HOMEPATH%/temptcrunij/lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"

echo "Running tcrunij now!"

java -Dlogback.configurationFile=./conf/logging-config.xml org.tcrun.cmd.Main %*
