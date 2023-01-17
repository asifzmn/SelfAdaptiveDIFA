#!/bin/bash
source ./xn_global.sh
ROOT=/home/xqfu
MAINCP="."

starttime=`date +%s%N | cut -b1-13`

java -Xmx10g -ea -cp ${MAINCP} methodAnalysis \
	diff0Available.txt  \
	test1/clientlog \
#	 1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
# echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

# echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

