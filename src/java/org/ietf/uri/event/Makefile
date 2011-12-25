#*********************************************************************
#
#  (C) 2001 The Virtual Light Company
#        http://www.vlc.com.au/
# 
# Package makefile for the org.ietf.uri.event directory
#
# Author: Justin Couch
# Version: $Revision: 1.1 $
#
#*********************************************************************

PACKAGE = org.ietf.uri.event

# The source files to compile. These are declared in order that they must be
# compiled in
SOURCE = ProgressEvent.java \
	ProgressListener.java \
	ProgressAdaptor.java \
	ProgressListenerMulticaster.java 

# The list of other files we need to copy from this directory to the classes
# directory when we are making JAR files.
EXTRA = 

# If the package requires extra classes outside the JDK, the JAR files that 
# are required are listed here. These should be found in ${PROJECT_ROOT}/lib
JARS_3RDPARTY =	

# Define IGNORE_CYCLES here because there is a known cyclic dependency in
# the EAI packages between org.ietf.uri.event and org.ietf.uri
IGNORE_CYCLES=1

include ${PROJECT_ROOT}/make/Makefile.jav
