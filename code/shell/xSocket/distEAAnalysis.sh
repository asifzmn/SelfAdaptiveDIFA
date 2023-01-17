#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 query traceDir number_of_inputs"
	exit 1
fi

#source ./chord_global.sh

#INDIR=$subjectloc/distEAoutdynComm
#INDIR=$subjectloc/OutTraces/
ROOT=/home/xqfu
subjectloc=/home/xqfu/xSocket
#INDIR=$subjectloc/distEAoutdyn
INDIR=$subjectloc/hcaitest
BINDIR=$subjectloc/java/bin
#BINDIR=$subjectloc/distEAInstrumented
query=${1:-"$subjectloc/distEAInstrumented/functionList.out"}
NT=${2:-"1"}

#MAINCP=".:/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$ROOT/workspace/DUAForensics/bin:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/mcia/bin"
#MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/jre/lib/tools.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/libs/mcia.jar:$/home/xqfu/chord/build/classes"
MAINCP=".:$ROOT/libs/DUA1.jar:$ROOT/libs/DistEA8.jar:$ROOT/libs/soot-trunk.jar"


starttime=`date +%s%N | cut -b1-13`

	#-debug
	#"-nonseparate"
java -Xmx4000m -ea -cp ${MAINCP} distEA.distEAAnalysis \
	"$query" \
	"$INDIR" \
	"$NT" \
	"-separate" \
	"-common" \
	"-nstrict" \
	"-prec" \
	"-debug"

stoptime=`date +%s%N | cut -b1-13`

echo "Time elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
