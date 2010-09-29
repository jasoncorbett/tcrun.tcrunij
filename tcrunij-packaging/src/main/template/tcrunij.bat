 @echo off
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ./lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"

java -Dlogback.configurationFile=./conf/logging-config.xml org.tcrun.cmd.Main %*
