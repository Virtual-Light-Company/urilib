#!/bin/sh

#
# Javadoc generation batch file
#
# Untested!!!!
#

echo "Starting URI Javadoc Generation"

DEST_DIR=./docs/uri
TITLE= \"URI Development Library\"

LOCAL_CLASSPATH=./

PKGS=org.ietf.uri org.ietf.uri.event
PKGS=$PKGS org.ietf.uri.content.text  org.ietf.uri.content.x_java
PKGS=$PKGS org.ietf.uri.protocol.file org.ietf.uri.protocol.data
PKGS=$PKGS org.ietf.uri.protocol.http org.ietf.uri.protocol.jar
PKGS=$PKGS org.ietf.uri.resolve
PKGS=$PKGS org.ietf.uri.resolve.file org.ietf.uri.resolve.thttp

javadoc -version -doctitle ${TITLE} -windowtitle ${TITLE} -classpath
${LOCAL_CLASSPATH} -d ${DEST_DIR} ${PKGS}

#
# REM Wait for the user to confirm
#

echo "Done"
