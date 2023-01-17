#!/bin/bash
ROOT=/home/xqfu
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:$ROOT/libs/DUA1.jar:$ROOT/libs/DistEA.jar:$ROOT/libs/soot-trunk.jar"
/usr/lib/jvm/java-8-openjdk-amd64/bin/javac -cp ${MAINCP} ChatServer/core/*.java

