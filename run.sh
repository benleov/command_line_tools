#!/bin/bash

# where the library jar files are contained
MYJARS=./lib

# add all jars in library path to classpath
CLASSPATH=$CLASSPATH$(find $MYJARS -name "*.jar" -exec printf :{} ';')

# where project classes are being built to
BUILD_DIR=./out/production/WebServicesAPI

# as curses uses stdout, we must specify a file for debug output
DEBUG_FILE=DEBUG.txt

java -Djcurses.protocol.filename=$DEBUG_FILE -cp "$BUILD_DIR/$CLASSPATH" webservicesapi.Main
