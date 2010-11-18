@echo off
rem This magic code found on http://stackoverflow.com/questions/524081/bat-file-to-create-java-classpath
setLocal EnableDelayedExpansion
set CLASSPATH="
 for /R ./lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"

echo "TCRunIJ will halt until a Java Debugger attaches on port 8000."

java -agentlib:jdwp=transport=dt_socket,server=y,address=8000 -Dlogback.configurationFile=./conf/logging-config.xml org.tcrun.cmd.Main %*
