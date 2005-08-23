#*********************************************************************
#
#  (C) 2001-05 Justin Couch
#    http://www.vlc.com.au/~justin/
#
# Lowest level common makefile for both native and Java code
# 
# Author: Justin Couch
# Version: $Revision: 1.5 $
#
#*********************************************************************

ifndef PROJECT_ROOT
  export PROJECT_ROOT=/usr/local/src/projects/uri
endif

ifndef JAVA_HOME
  JAVA_HOME=/usr/local/java/jdk1.3
endif

ifndef APP_ROOT
  BUILD_ROOT_DIR=$(PROJECT_ROOT)
else
  BUILD_ROOT_DIR=$(APP_ROOT)
endif

SRC_DIR=$(BUILD_ROOT_DIR)/src
DOCS_DIR=$(BUILD_ROOT_DIR)/docs
LIB_DIR=$(BUILD_ROOT_DIR)/lib
MAKE_DIR=$(PROJECT_ROOT)/make

JAVA_DIR=$(SRC_DIR)
PARSER_DIR=$(SRC_DIR)/javacc
NATIVE_DIR=$(SRC_DIR)/native
INCLUDE_DIR=$(NATIVE_DIR)/include
EXAMPLES_DIR=$(PROJECT_ROOT)/examples

DELETE    = rm -f
MAKEDIR   = mkdir -p
RMDIR     = rm -fr
PRINT     = @echo
COPY      = cp 
COPY_PATH = cp --parents
CHMOD     = chmod
CD        = cd

#JAVAC_FLAGS=-deprecation
#JAVADOC_FLAGS=

MAKEFLAGS=-s
