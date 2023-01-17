#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

ROOT=/home/xqfu/
subjectloc=/home/xqfu/xSocket

MAINCP=".:/home/hcai/tools/jdk160/jre/lib/rt.jar:$ROOT/DistEA/DUA1.jar:/home/hcai/workspace/mcia/bin:/home/hcai/libs/polyglotclasses-1.3.5.jar:/home/hcai/libs/sootclasses-2.5.0.jar:/home/hcai/libs/jasminclasses-2.5.0.jar:/home/hcai/tools/java_cup.jar"

SOOTCP=".:/home/hcai/tools/jdk160/jre/lib/rt.jar:/home/hcai/workspace/mcia/bin:/home/xqfu/xSocket/java/bin:/home/xqfu/xSocket/bin"

suffix="xSocket"

LOGDIR=out-diverInstr
mkdir -p $LOGDIR
logout=$LOGDIR/instr-$suffix.out
logerr=$LOGDIR/instr-$suffix.err

OUTDIR=$subjectloc/DiverInstrumented
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
java -Xmx100g -ea -cp ${MAINCP} Diver.DiverInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:true,rta:true -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-wrapTryCatch \
    -intraCD \
    -interCD \
	-allowphantom \
    -exInterCD \
	-serializeVTG \
	-slicectxinsens \
    -process-dir /home/xqfu/xSocket/java/bin  \
    -process-dir /home/xqfu/xSocket/bin  \
	1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

