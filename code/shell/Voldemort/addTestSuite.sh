#!/bin/bash
#cat ./vdtestClasses.txt | awk '{print $1}' | sort | uniq | \
cat ./vdtestClasses-integrate.txt | awk '{print $1}' | sort | uniq | \
	awk '{printf("testClses.add (%s.class);\n", $1);}'
	#awk '{printf("%s.class,\n", $1);}'
	#awk '{printf("suite.addTestSuite(%s.class);\n", $1);}'

	#awk '{printf("suite.addTestSuite(Class.forName(\"%s\"));\n", $1);}'

# hcai vim :set ts=4 tw=4 tws=4

