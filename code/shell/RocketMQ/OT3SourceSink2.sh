#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./rk_global.sh
# ROOT=/home/xqfu
MAINCP="$ROOT/DUA6NODUAA.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar"

#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/binBK1:$ROOT/FLOWDIST.jar"

SOOTCP=".:$subjectloc/bin:$ROOT/FLOWDIST.jar"
suffix="SourceSink"

LOGDIR=out-DTInstr
rm -R $LOGDIR -f
mkdir -p $LOGDIR
logout=$LOGDIR/DT2SourceSink-$suffix.out
logerr=$LOGDIR/DT2SourceSink-$suffix.err

INDIR=$subjectloc/bin
OUTDIR=$LOGDIR
# mkdir -p $OUTDIR -f
cp methodList.out $INDIR
cp $subjectloc/OTBrPre/stmtCoverage1.out $INDIR
cp $subjectloc/OTBrPre/stmtids.out $INDIR
rm source*2.txt -f
rm sink*2.txt -f
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
	
java -Xmx400g -ea -cp ${MAINCP} disttaint.dt2SourceSink2 \
	$OUTDIR \
	$INDIR \
	"-method" \
	"-stmt" \
#    	 1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

