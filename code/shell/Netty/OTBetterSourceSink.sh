#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./ne_global.sh
ROOT=/home/xqfu
MAINCP="/home/xqfu/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar"

SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/4119:$ROOT/FLOWDIST.jar"

suffix="SourceSink"

LOGDIR=out-DTInstr
mkdir -p $LOGDIR
logout=$LOGDIR/SourceSink-$suffix.out
logerr=$LOGDIR/SourceSink-$suffix.err


INDIR=$subjectloc/OTInstrumented
OUTDIR=/tmp
mkdir -p $OUTDIR
rm source_1.txt -f
rm sink_1.txt -f
rm s*DiffClass.txt -f
rm s*Pair.txt -f
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

	java -Xmx100g -ea -cp ${MAINCP} disttaint.OTBetterSourceSink \
	$subjectloc/4119 \
#	 1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4
