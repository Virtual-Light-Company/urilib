@echo off
rem
rem Javadoc generation batch file
rem

SETLOCAL

echo Starting URI Javadoc Generation

title Building URI Javadocs

echo Starting URI Javadoc Generation

set DEST_DIR=Enter Your Destination DIR HERE
set TITLE= "URI Development Library (v0.7)"

set LOCAL_CLASSPATH=c:\Justin\projects\uri\src

set PKGS=org.ietf.uri org.ietf.uri.event
set PKGS=%PKGS% org.ietf.uri.content.text org.ietf.uri.content.x_java
set PKGS=%PKGS% org.ietf.uri.protocol.file
set PKGS=%PKGS% org.ietf.uri.protocol.data
set PKGS=%PKGS% org.ietf.uri.protocol.http 
set PKGS=%PKGS% org.ietf.uri.protocol.jar
REM org.ietf.uri.protocol.https org.ietf.uri.protocol.shttp
set PKGS=%PKGS% org.ietf.uri.resolve
set PKGS=%PKGS% org.ietf.uri.resolve.file org.ietf.uri.resolve.thttp

javadoc -version -doctitle %TITLE% -windowtitle %TITLE% -classpath %LOCAL_CLASSPATH% -d %DEST_DIR% %PKGS%

REM
REM Wait for the user to confirm
REM

ENDLOCAL

pause

