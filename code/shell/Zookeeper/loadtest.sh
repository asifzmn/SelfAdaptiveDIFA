#!/bin/bash
java -jar \
	build/contrib/fatjar/zookeeper-dev-fatjar.jar \
	generateLoad \
	cse-rsws-01.cse.nd.edu:2181 \
	/sysTest 10 10 100
