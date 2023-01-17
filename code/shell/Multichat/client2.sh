#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./mc_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/bin/

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar:$ROOT/tools/DUA1.jar:$ROOT/tools/DUAForensics-bins-code/InstrReporters:$ROOT/tools/DistEA/bin/:$ROOT/DistEA/DUA-forensics.jar:$ROOT/DistEA/DistEA.jar:$ROOT/DistEA/libs/jasminclasses-2.3.0.jar:$ROOT/DistEA/libs/polyglot.jar:$ROOT/DistEA/libs/sootclasses-2.3.0.jar:$ROOT/DistEA/libs/java_cup.jar"

suffix="multi-chat"

OUTDIR=distEAoutdyn
mkdir -p $OUTDIR

MAINCLS="ChatServer.core.MainServer"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
java -Xmx40g -ea -DltsDebug=false -DuseToken=false \
	-cp ${MAINCP} \
	${MAINCLS} \

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
