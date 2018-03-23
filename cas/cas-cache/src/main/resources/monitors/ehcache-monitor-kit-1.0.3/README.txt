README
======

The Ehcache-monitor module provides monitoring and management capabilities for Ehcache. 

This package contains a probe, which gets deployed in your application, and a monitor, which is deployed on a monitoring server. The console has a Web interface and also an XML over HTTP interface for integration with monitoring tools. The probe is compatible with all Ehcache versions 1.5 and higher, running on JDK 1.5 and 1.6.
 
Probe Installation Instructions
-------------------------------

To include the probe in your Ehcache application:

1.  Add the ehcache-probe-<version>.jar to your application classpath (or war file). Do this in the same way you added the core ehcache jar to your application.

    For Maven users, to use the probe jar in a Maven project, you will need to install it to your local Maven repository.

    mvn install:install-file -Dfile=lib/ehcache-probe-<version>.jar \
                             -Dpackaging=jar \
                             -DgroupId=org.terracotta \
                             -DartifactId=ehcache-probe \
                             -Dversion=<version>

    Then you can add this dependency into your pom.xml:

       <dependency>
         <groupId>org.terracotta</groupId>
         <artifactId>ehcache-probe</artifactId>
         <version>[version]</version>
       </dependency>

2.  In ehcache.xml, configure the Ehcache probe to communicate with the monitor by specifying the class name of the probe,
    the address (or hostname), the port that the monitor will be running on and whether to perform memory management.

    This is done as per the following example:

    <cacheManagerPeerListenerFactory class="org.terracotta.ehcachedx.monitor.probe.ProbePeerListenerFactory"
    properties="monitorAddress=localhost, monitorPort=9889, memoryMeasurement=true"/>

3.  Logging

    Ehcache 1.7.1 and above require SLF4J. Earlier versions used commons logging. The probe, like all new Ehcache modules,
    uses SLF4J, which is becoming a new standard in open source projects.

    If you are using Ehcache 1.5 to 1.7.0, you will need to add slf4j-api and one concrete logger.

    If you are using Ehcache 1.7.1 and above you should not need to do anything because you will already be using
    slf4j-api and one concrete logger.

    More information on SLF4J is available from http://www.slf4j.org.


Starting the Monitor
--------------------

To start the monitor, run the startup script provided in the bin directory: startup.sh on Unix and startup.bat on Microsoft Windows. The monitor port selected in this script should match the port specified in ehcache.xml.

The monitor can be configured, including interface, port and security settings, in the etc/ehcache-monitor.conf. 

Using the Monitor's Web Interface
---------------------------------

Once the server is running, point your web browser to it to see the status and configuration of the cache(s) being monitored. For example, if the monitor is running on localhost on port 9889, use the following URL:
http://localhost:9889/monitor

Using the Monitor's XML Interface
---------------------------------

To see the XML API: 
http://localhost:9889/monitor/list

More Information
----------------

More documentation is available by clicking the Help button on the Monitor home page.

Licensing
---------
Unless otherwise indicated, this module is licensed for usage in development. For details see the license terms in the appropriate LICENSE.txt. To obtain a commercial license for use in production, please contact sales@terracottatech.com

Copyright (c) 2009, Terracotta, Inc.
http://www.terracotta.org

