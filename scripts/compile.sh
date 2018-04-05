#!/bin/sh
cd src
rmiregistry &
javac $(find . -name "*.java")
mkdir SharedFiles
