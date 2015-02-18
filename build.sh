#!/bin/bash

OUTDIR=bin
ROOT=$(cd $(dirname $0); pwd)
CLASSPATH=${ROOT}/lib/*:
SRCDIR=${ROOT}/src

if [ -z "$OUTDIR" ]; then
  echo "Usage: $0 <output-directory>"
  echo "    Builds the server in <output-directory>."
  exit 1
fi
build_needed=yes

# If nothing has changed since the last build in the same directory, quit.
if [ -f ${OUTDIR}/built ]; then
  if [ -z "$(find ${SRCDIR} -name '*.java' -newer ${OUTDIR}/built)" ]; then
    echo "No .java files changed since last build; ${OUTDIR} is up to date."
    build_needed=no
  fi
fi

# Compile all the source files into class files.
if [ ${build_needed} == yes ]; then
  mkdir -p ${OUTDIR}
  echo "Compiling..."
  javac -cp ${CLASSPATH} $(find ${SRCDIR} -name '*.java') -d ${OUTDIR} || exit 1
  touch ${OUTDIR}/built
  echo "...class files written to ${OUTDIR} successfully."
fi
