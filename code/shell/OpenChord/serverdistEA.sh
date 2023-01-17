#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./chord_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/distEAInstrumented/

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DUA1.jar:$ROOT/distEADIVER.jar:$INDIR:$subjectloc/config:$subjectloc/lib/log4j.jar"

suffix="chord"

OUTDIR=$subjectloc/distEAoutdyn/
mkdir -p $OUTDIR

MAINCLS="de.uniba.wiai.lspi.chord.console.Main"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
java -Xmx20g -ea -DltsDebug=true -DuseToken=true -DtrackSender=true \
	-cp ${MAINCP} \
	${MAINCLS} \

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
