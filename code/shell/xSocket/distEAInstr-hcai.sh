#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

ROOT=/home/xqfu
subjectloc=/home/xqfu/xSocket

#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DistEA/DUA1.jar:$ROOT/libs/DistEA5.jar::$ROOT/libs/soot-trunk.jar"
#MAINCP=".:/home/hcai/tools/jdk160/jre/lib/rt.jar:$ROOT/DistEA/DUA1.jar:/home/hcai/workspace/mcia/bin::/home/hcai/libs/soot-trunk.jar"
MAINCP=".:/home/hcai/tools/jdk160/jre/lib/rt.jar:$ROOT/DistEA/DUA1.jar:/home/hcai/workspace/mcia/bin::/home/hcai/libs/polyglotclasses-1.3.5.jar:/home/hcai/libs/sootclasses-2.5.0.jar:/home/hcai/libs/jasminclasses-2.5.0.jar:/home/hcai/tools/java_cup.jar"

#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/DistEA5.jar:$subjectloc/bin"
SOOTCP=".:/home/hcai/tools/jdk160/jre/lib/rt.jar:/home/hcai/workspace/mcia/bin:/home/xqfu/xSocket/java/bin:/home/xqfu/xSocket/bin"

suffix="xSocket"

LOGDIR=out-distEAInstr
mkdir -p $LOGDIR
logout=$LOGDIR/instr-$suffix.out
logerr=$LOGDIR/instr-$suffix.err

OUTDIR=$subjectloc/distEAInstrumented
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`

	#-allowphantom \
   	#-duaverbose \
	#-wrapTryCatch \
	#-dumpJimple \
	#-statUncaught \
	#-perthread \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \
	#-syncnio \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \
	#-dumpJimple \
	#-dumpFunctionList \
java -Xmx50g -ea -cp ${MAINCP} distEA.distEAInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:true,rta:true -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-allowphantom \
	-socket \
	-nio \
	-wrapTryCatch \
	-slicectxinsens \
	-process-dir   /home/xqfu/xSocket/java/bin \
	-process-dir   /home/xqfu/xSocket/bin \
	1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

