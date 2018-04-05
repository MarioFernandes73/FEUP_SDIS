#!/bin/sh

rmiregistry &
java src/server/Main 1.0 1 //localhost/peer1 224.0.0.0 8000 224.0.0.0 8001 224.0.0.0 8002
