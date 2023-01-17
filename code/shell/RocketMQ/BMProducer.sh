#!/bin/bash
export NAMESRV_ADDR=localhost:9876

source ./rk_global.sh

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:/home/xqfu/rocketmq/420/lib/rocketmq-example-4.2.0.jar"
for file in /home/xqfu/rocketmq/420/lib/*.jar;
do
	MAINCP=$MAINCP:$file
done

MAIN="org.apache.rocketmq.example.benchmark.Producer"

java -Xmx2G -ea \
	-cp ${MAINCP} \
	${MAIN} \

