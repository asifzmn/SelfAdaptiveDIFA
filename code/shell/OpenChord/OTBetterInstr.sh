#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi
source ./chord_global.sh
ROOT=/home/xqfu
# MAINCP="$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar"
MAINCP=".:$ROOT/FLOWDIST.jar:/home/xqfu/DUA1.jar:$ROOT/libs/soot-trunk.jar"

SOOTCP=".:$subjectloc/build/classes:$ROOT/FLOWDIST.jar"

suffix="chord"

LOGDIR=out-OTInstr
rm -R -f $LOGDIR
logout=$LOGDIR/instr-$suffix.out
logerr=$LOGDIR/instr-$suffix.err

OUTDIR=$subjectloc/OTInstrumented
rm -R -f $LOGDIR
rm -R -f $OUTDIR
mkdir -p $LOGDIR
mkdir -p $OUTDIR
# OUTDIR=$LOGDIR

# cp methodList.out $INDIR
# cp $subjectloc/OTBrPre/stmt*.out $INDIR

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
	
java -Xmx400g -ea -cp ${MAINCP} disttaint.OTBetterInstr \
	-w -cp $SOOTCP -p cg verbose:false,implicit-entry:false \
	-p cg.spark verbose:false,on-fly-cg:true,rta:false -f c \
	-d $OUTDIR \
	-brinstr:off -duainstr:off \
	-allowphantom \
	-wrapTryCatch \
            -interCD \
            -interCD \
            -exInterCD \
        -duaverbose   \
        -serializeVTG \
	-slicectxinsens \
        -process-dir $subjectloc/build/classes \
	 #1> $logout 2> $logerr

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
# cp $subjectloc/build/classes/de/uniba/wiai/lspi/util/console/ConsoleThread.class $subjectloc/DT2Instrumented/de/uniba/wiai/lspi/util/console/ConsoleThread.class
echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

