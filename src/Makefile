#*********************************************************************
#
#  (C) 2001 The Virtual Light Company
#        http://www.vlc.com.au/
# 
# Package makefile for the org.ietf.uri.event directory
#
# Author: Justin Couch
# Version: $Revision: 1.4 $
#
#*********************************************************************

# Names of packages to be compiled. These are declared in the order that
# they are to be compiled in. We create one at a time
BUILD_ORDER = \
  org.ietf.uri.resolve \
  org.ietf.uri.event \
  org.ietf.uri \
  vlc.net.content.text \
  vlc.net.content.x_java \
  vlc.net.resolve.file \
  vlc.net.resolve.thttp \
  vlc.net.protocol.data \
  vlc.net.protocol.file \
  vlc.net.protocol.http \
  vlc.net.protocol.https \
  vlc.net.protocol.shttp \
  vlc.net.protocol.jar 

# Names of packages that we want included in the javadoc
JAVADOC_PACKAGES = \
  org.ietf.uri.resolve \
  org.ietf.uri.event \
  org.ietf.uri \
  vlc.net.content.text \
  vlc.net.content.x_java \
  vlc.net.resolve.file \
  vlc.net.resolve.thttp \
  vlc.net.protocol.data \
  vlc.net.protocol.file \
  vlc.net.protocol.http \
  vlc.net.protocol.https \
  vlc.net.protocol.shttp \
  vlc.net.protocol.jar 

# Names of JAR files we want to create
JARS = uri.jar vlc_uri.jar

# 3rd party libraries that need including.
JARS_3RDPARTY= httpclient.jar gnu-regexp.jar

# The list of other files we need to copy from this directory to the classes
# directory when we are making JAR files.
EXTRA = 

# Definitions of setup information if we are making a self-executable JAR
MAIN_CLASS     = 
MAIN_PACKAGE   = 
MAIN_JAR       = uri.jar

#*********************************************************************
#
# Javadoc
#
#*********************************************************************

OVERVIEW    = overview.html
WINDOWTITLE = 'URI Class Library Specification'
DOCTITLE    = 'URI Classes API'
HEADER      = 'URI Classes API'
BOTTOM      = '<center><font size=2>Copyright &copy; 1999 - 2001 \
		       <a href=\"http://www.vlc.com.au\">The Virtual Light Company</a> \
               </font></center>'

# A list of the local directories that contain other Javadoc that we want
# to link to. For example we link to the JDK, JAXP, SAI and JUnit Javadocs
# here. Note that these *must* be full URLs and not just local directory 
# references. If your directory path contains a space in it, you may be in
# trouble. We haven't tested it with that. Try using double quotes inside
# the single quotes to get the space treated in the file name
LINK_URLS   = 

include $(PROJECT_ROOT)/make/Makefile.jav 
