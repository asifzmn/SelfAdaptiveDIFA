#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./ne_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/DiverInstrumented/

MAINCP=".:$ROOT/libs/DUA1.jar:$ROOT/DiverThread.jar:$ROOT/libs/soot-trunk.jar:$INDIR"


suffix="nio-echo"

OUTDIR=$subjectloc/Diveroutdyn2
mkdir -p $OUTDIR

MAINCLS="NioClient"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
java -Xmx40g -ea -DltsDebug=true -DuseToken=false \
	-cp ${MAINCP} \
	${MAINCLS} \
	10.99.1.190 \
	9090

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
