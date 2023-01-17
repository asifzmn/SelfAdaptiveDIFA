#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 query traceDir number_of_inputs"
	exit 1
fi

source ./vd_global.sh

INDIR=$subjectloc
#INDIR=$subjectloc/distEAoutdyn

BINDIR=$subjectloc/bin
query=${1:-"$subjectloc/distEAInstrumented/functionList.out"}
NT=${2:-"1"}

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk.jar:$ROOT/libs/DUA1.jar:$ROOT/libs/distEADIVER.jar:$INDER:$BINDIR"
starttime=`date +%s%N | cut -b1-13`

	#-debug
java -Xmx90800m -ea -cp ${MAINCP} distEA.distEADIVERAnalysis \
	"$query" \
	"$INDIR" \
	"$NT" \
	"-separate" \
	"-common" \
	"-nstrict" \
	"-debug"

stoptime=`date +%s%N | cut -b1-13`

echo "Time elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
