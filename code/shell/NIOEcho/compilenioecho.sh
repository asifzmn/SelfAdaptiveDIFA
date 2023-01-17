#!/bin/bash
ROOT=/home/xqfu
# MAINCP=MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/libs/mcia.jar:$ROOT/libs/soot-trunk.jar"
javac -cp -Xlint:unchecked src/*.java -d `pwd`/bin

