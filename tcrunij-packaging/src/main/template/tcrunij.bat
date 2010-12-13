 @echo off
 rem This magic code found on http://stackoverflow.com/questions/524081/bat-file-to-create-java-classpath
 setLocal EnableDelayedExpansion
 set CLASSPATH="
 for /R ./lib %%a in (*.jar) do (
   set CLASSPATH=!CLASSPATH!;%%a
 )
 set CLASSPATH=!CLASSPATH!"

java -Dlogback.configurationFile=./conf/logging-config.xml org.tcrun.cmd.Main %*
