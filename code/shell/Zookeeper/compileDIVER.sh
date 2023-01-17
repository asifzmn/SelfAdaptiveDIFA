#!/bin/bash
ROOT=/home/xqfu
MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/libs/DUA1.jar:$ROOT/libs/mcia.jar::$ROOT/libs/soot-trunk.jar:/home/xqfu/z3411/build/zookeeper-3.4.11.jar:/home/xqfu/z3411/build/classes:/home/xqfu/z3411"
echo $MAINCP
/opt/jdk1.8.0_101/bin/javac -Xlint:unchecked -cp ${MAINCP} AllTestsSelect.java

