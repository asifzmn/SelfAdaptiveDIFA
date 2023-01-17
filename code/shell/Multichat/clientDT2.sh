#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./mc_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/DT2Instrumented/

MAINCP=".:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/DT2Instrumented"

suffix="multi-chat"

OUTDIR=$subjectloc/DT2outdyn/
mkdir -p $OUTDIR

MAINCLS="ChatClient.core.MainClient"

starttime=`date +%s%N | cut -b1-13`
set JAVA_OPTS=-DltsRunDiver=true
java -Xmx4g -ea -DltsDebug=true -DuseToken=true -DltsRunDiver=true \
	-cp ${MAINCP} \
	${MAINCLS} \
	10.99.1.191

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
