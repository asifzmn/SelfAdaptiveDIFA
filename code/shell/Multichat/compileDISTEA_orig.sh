#!/bin/bash
ROOT=/home/xqfu
MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/DUA1.jar:$ROOT/DistEA/DistEA.jar::$ROOT/libs/soot-trunk.jari:/home/xqfu/src"
/opt/jdk1.8.0_101/bin/javac -cp ${MAINCP} ChatServer/core/*.java

