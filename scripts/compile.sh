#!/bin/sh

cd src

javac $(find . -name "*.java")

mkdir SharedFiles
