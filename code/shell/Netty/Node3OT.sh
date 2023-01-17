#!/bin/bash
source ./ne_global.sh
ROOT=/home/xqfu
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/netty/commons-codec-1.10.jar:/home/xqfu/netty/activation.jar.jar:/home/xqfu/netty/mail.jar:/home/xqfu/netty/OTInstrumented:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:"

echo $MAINCP
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} Node3 
stoptime=`date +%s%N | cut -b1-13`
echo "Time elapsed: " `expr $stoptime - $starttime` milliseconds 

# java -cp ".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/netty/netty-all-4.1.19.Final.jar:/home/xqfu/netty/commons-codec-1.10.jar:/home/xqfu/netty/activation.jar:/home/xqfu/netty/mail.jar:/home/xqfu/java" Node3
