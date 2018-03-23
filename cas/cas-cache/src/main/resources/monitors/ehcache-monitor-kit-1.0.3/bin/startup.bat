@echo off

if not defined JAVA_HOME (
  echo JAVA_HOME environment variable must be set
  exit /b 1
)

setlocal enabledelayedexpansion
set JAVA_HOME="%JAVA_HOME:"=%"
set PRGDIR=%~d0%~p0..
set PRGDIR="%PRGDIR:"=%"
set classpath=
for %%F in (%PRGDIR%\lib\*.jar) do ( 
  set classpath=!classpath!;%%F%
) 

%JAVA_HOME%\bin\java ^
   %JAVA_OPTS% ^
   -server ^
   -Dehcachedx.sampling.seconds=10 ^
   -Dehcachedx.sampling.history=8640 ^
   -server ^
   -cp %classpath% ^
   org.terracotta.ehcachedx.monitor.Monitor start ^
   -f %PRGDIR%\etc\ehcache-monitor.conf %*

endlocal   
