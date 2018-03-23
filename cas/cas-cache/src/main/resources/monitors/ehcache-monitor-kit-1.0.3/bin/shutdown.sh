#!/bin/sh

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
   -cp "$classpath" \
   org.terracotta.ehcachedx.monitor.Monitor stop -f "$PRGDIR/etc/ehcache-monitor.conf" "$@"
