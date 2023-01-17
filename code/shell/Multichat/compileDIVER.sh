#!/bin/bash
ROOT=/home/xqfu
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/mcia.jar:$ROOT/libs/soot-trunk.jar:/home/xqfu/multichat/src"
/usr/lib/jvm/java-8-openjdk-amd64/bin/javac -cp ${MAINCP} /home/xqfu/multichat/src/ChatServer/core/*.java
/usr/lib/jvm/java-8-openjdk-amd64/bin/javac -cp ${MAINCP} /home/xqfu/multichat/src/ChatServer/handler/*.java

/usr/lib/jvm/java-8-openjdk-amd64/bin/javac -cp ${MAINCP} /home/xqfu/multichat/src/ChatClient/core/*.java


