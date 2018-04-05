#!/bin/sh
cd src
java cli/TestApp //localhost/peer1 BACKUP test.jpg 2
#java src/cli/TestApp //localhost/peer1 RESTORE example.jpg
#java src/cli/TestApp //localhost/peer1 DELETE example.jpg
#java src/cli/TestApp //localhost/peer1 RECLAIM 0
#java src/cli/TestApp //localhost/peer1 STATE

