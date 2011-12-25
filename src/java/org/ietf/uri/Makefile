#*********************************************************************
#
#  (C) 2001 The Virtual Light Company
#        http://www.vlc.com.au/
# 
# Package makefile for the org.ietf.uri directory
#
# Author: Justin Couch
# Version: $Revision: 1.2 $
#
#*********************************************************************

PACKAGE = org.ietf.uri

# The source files to compile. These are declared in order that they must be
# compiled in
SOURCE =  URIConstants.java \
    MalformedURNException.java \
	NoURNBindingsException.java \
	NoURNConfigException.java \
	IllegalActionException.java \
	UnsupportedServiceException.java \
	UnresolvableURIException.java \
	UnknownProtocolException.java \
	TwoWayHashMap.java \
	StringEnumerator.java \
	FileNameMap.java \
	DefaultFileMap.java \
	ContentHandler.java \
	ContentHandlerFactory.java \
	ResourceConnection.java \
	URIResourceFactory.java \
	URIResourceFactoryImpl.java \
	URIResourceStream.java \
	URIResourceStreamFactory.java \
	URIResolverService.java \
	URIResolverServiceFactory.java \
	URNResolverService.java \
	URLResourceConnection.java \
	FtpResourceConnection.java \
	HttpResourceConnection.java \
	JarResourceConnection.java \
	JavaNetContentHandlerWrapper.java \
	JavaNetURLConnectionWrapper.java \
	URLConnectionWrapper.java \
	URLConnectionWrapperResourceStream.java \
	RDSManager.java \
	ResourceManager.java \
	ProgressUpdateEvent.java \
	URI.java \
	URN.java \
	URL.java \
	URC.java \
	URIUtils.java

# The list of other files we need to copy from this directory to the classes
# directory when we are making JAR files.
EXTRA = 

# If the package requires extra classes outside the JDK, the JAR files that 
# are required are listed here. These should be found in ${PROJECT_ROOT}/lib
JARS_3RDPARTY =	

include ${PROJECT_ROOT}/make/Makefile.jav
