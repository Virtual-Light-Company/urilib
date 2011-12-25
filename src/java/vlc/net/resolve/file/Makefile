#*********************************************************************
#
#  (C) 2001 The Virtual Light Company
#        http://www.vlc.com.au/
# 
# Package makefile for the vlc.net.resolve.file directory
#
# Author: Justin Couch
# Version: $Revision: 1.1 $
#
#*********************************************************************

PACKAGE = vlc.net.resolve.file

# The source files to compile. These are declared in order that they must be
# compiled in
SOURCE = InvalidRegexException.java \
	NoSuchGroupException.java \
	Resource.java \
	GroupResource.java \
	NamespaceResolver.java \
	FileParser.java \
	Resolver.java \

# If the package requires extra classes outside the JDK, the JAR files that 
# are required are listed here. These should be found in ${PROJECT_ROOT}/lib
JARS_3RDPARTY =	gnu-regexp-1.0.8.jar

# The list of other files we need to copy from this directory to the classes
# directory when we are making JAR files.
EXTRA = 

include ${PROJECT_ROOT}/make/Makefile.jav
