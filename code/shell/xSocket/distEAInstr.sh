#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi
ROOT=/home/xqfu
subjectloc=/home/xqfu/xSocket
MAINCP=".:$ROOT/libs/DUA1.jar:$ROOT/libs/DistEA8.jar:$ROOT/libs/soot-trunk.jar"

SOOTCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/home/xqfu/xSocket/java/bin:/home/xqfu/xSocket/bin:$ROOT/libs/DistEA8.jar"

suffix="xc"

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
	#-socket \
	#-nio \
	#-dumpJimple \
	#-dumpFunctionList \
java -Xmx4000m -ea -cp ${MAINCP} distEA.distEAInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:false,rta:true -f c \
	-d $OUTDIR \
	-brinstr:on -duainstr:on \
	-allowphantom \
	-nio \
	-syncnio \
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

