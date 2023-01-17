#!/bin/bash
export NAMESRV_ADDR=localhost:9876

source ./rk_global.sh

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:/home/xqfu/rocketmq/OTInstrumented:$ROOT/libs/soot-trunk.jar"
for file in /home/xqfu/rocketmq/OTInstrumented/*.jar;
do
	MAINCP=$MAINCP:$file
done
starttime=`date +%s%N | cut -b1-13`
MAIN="org.apache.rocketmq.example.benchmark.Consumer"

java -Xmx2G -ea \
	-cp ${MAINCP} \
	${MAIN} \

stoptime=`date +%s%N | cut -b1-13`
echo "Time elapsed: " `expr $stoptime - $starttime` milliseconds 
