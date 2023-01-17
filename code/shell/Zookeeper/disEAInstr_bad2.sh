#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./zk_global.sh
ROOT="/home/xqfu"
MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/DistEA/DUA1.jar:$ROOT/DistEA/DistEA.jar::$ROOT/libs/soot-trunk4.jar:$subjectloc/bin"

SOOTCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:$ROOT/DistEA/DistEA.jar:$subjectloc/bin:$subjectloc/build/classes:$subjectloc/build/test/classes:$subjectloc/contrib/fatjar/classes:$subjectloc/lib/netty-3.7.0.Final.jar:$ROOT/DistEA/DistEA.jar:$ROOT/DistEA/DUA1.jar:$ROOT/libs/polyglotclasses-1.3.5.jar:$ROOT/libs/jasminclasses-2.5.0.jar:$ROOT/libs/heros-1.0.0-SNAPSHOT.jar:$ROOT/libs/soot-trunk4.jar:$subjectloc/bin"

for i in /home/xqfu/libs/*.jar;
do
	SOOTCP=$SOOTCP:$i
done

suffix="zk"

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
	#-main-class org.apache.zookeeper.util.FatJarMain \
	#-entry:org.apache.zookeeper.util.FatJarMain \
	#-process-dir $subjectloc/build/contrib/fatjar/classes \
java -Xmx10g -ea -cp ${MAINCP} distEA.distEAInst \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:false,rta:true -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-nio \
	-socket \
	-wrapTryCatch \
	-dumpFunctionList \
	-slicectxinsens \
	-process-dir $subjectloc/build/classes \
	-process-dir $subjectloc/build/test/classes \
	1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

#echo "Instrumentation done, now copying resources required for running."
#cp -rf $subjectloc/build/classes/org/apache/fop/cli/image2fo.xsl $OUTDIR/org/apache/fop/cli/
#cp -rf $subjectloc/build/classes/org/apache/fop/pdf/*.icm* $OUTDIR/org/apache/fop/pdf/
#cp -rf $subjectloc/build/classes/org/apache/fop/render/awt/viewer/{images,resources} $OUTDIR/org/apache/fop/render/awt/viewer/
#cp -rf $subjectloc/build/classes/META-INF $OUTDIR/

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

