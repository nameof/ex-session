#!/bin/sh
#
# #Usage: (START|STOP) [-l <license file location>] [-i <host ip>] [-p <host port>] [-f <config file location>] [-u <user name>] [-w <password>] [-h]



PRGDIR=`dirname "$0"`/..

if test \! -d "${JAVA_HOME}"; then
  echo "JAVA_HOME environment variable must be set"
  exit 1
fi

classpath=""
for i in $PRGDIR/lib/*.jar; do
  classpath=$i:$classpath
done

"${JAVA_HOME}/bin/java" \
   ${JAVA_OPTS} \
   -server \
   -cp "$classpath" \
   org.terracotta.ehcachedx.monitor.Monitor start \
   -j "$PRGDIR/etc/jetty.xml" \
   -f "$PRGDIR/etc/ehcache-monitor.conf" "$@"
