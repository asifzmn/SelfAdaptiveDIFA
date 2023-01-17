#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./ne_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/distEAInstrumented/

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/DistEA.jar::$ROOT/libs/soot-trunk.jar:$INDIR"

suffix="nio-echo"

OUTDIR=$subjectloc/distEAoutdyn
mkdir -p $OUTDIR

MAINCLS="NioServer"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
java -Xmx40g -ea -DltsDebug=true -DuseToken=false \
	-cp ${MAINCP} \
	${MAINCLS} \
	9090 \
        10.99.1.190	

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
