#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./th_global.sh

MAINCP=".:$subjectloc/DTInstrumented:$ROOT/libs/soot-trunk.jar:$ROOT/DUA1.jar:$ROOT/FlowDist.jar"
echo $MAINCP
for i in $subjectloc/0110/lib/java/build/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done
echo $MAINCP
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} CalculatorClient
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds 



