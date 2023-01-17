#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi
ROOT=/home/xqfu
subjectloc=/home/xqfu/thrift
#MAINCP=".:$ROOT/libs/DUA1.jar:$ROOT/libs/DistEA.jar:$ROOT/libs/soot-trunk.jar"

MAINCP=".:$ROOT/DUA1.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DistEA8.jar"
#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/thrift/0110/lib/java/build:/home/xqfu/thrift/java3/bin:$ROOT/libs/DistEA5.jar"

SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/thrift/0100/lib/java/build:/home/xqfu/thrift/java/bin:$ROOT/DistEA8.jar"
suffix="thrift"

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
	#-syncnio 
	#-dumpJimple \
	#-dumpFunctionList \

java -Xmx100g -ea -cp ${MAINCP} distEA.distEAInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:false,rta:true -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-allowphantom \
	-socket \
	-nio \
	-wrapTryCatch \
	-slicectxinsens \
	-dumpJimple \
	-dumpFunctionList \
    -process-dir  /home/xqfu/thrift/0100/lib/java/build  \
         -process-dir  /home/xqfu/thrift/java/bin  \
#	1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0




