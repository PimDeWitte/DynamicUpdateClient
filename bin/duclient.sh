#!/bin/bash

OUTDIR=.
ROOT=$(cd $(dirname $0); pwd)
CLASSPATH=../lib/*
MAINCLASS=StartUpdateClient

cd ${OUTDIR}

# Start the server.
java -cp .:${CLASSPATH} ${MAINCLASS} $1
