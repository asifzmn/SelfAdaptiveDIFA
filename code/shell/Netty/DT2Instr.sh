#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./ne_global.sh
ROOT=/home/xqfu/
#DRIVERCLASS=ChatServer.core.MainServer
#subjectloc=$subjectloc


# MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:$ROOT/FLOWDIST.jar"
MAINCP=".:$ROOT/FLOWDIST.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/xqfu/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar"
echo $MAINCP

rm -R out-DT2Instr -f
mkdir -p out-DT2Instr



SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/java:$subjectloc/4119:$ROOT/FLOWDIST.jar"


echo $SOOTCP
OUTDIR=$subjectloc/DT2Instrumented
rm -R $OUTDIR -f
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`
	#-sclinit \
	#-wrapTryCatch \
	#-debug \
	#-dumpJimple \
	#-statUncaught \
	#-ignoreRTECD \
	#-exInterCD \
	#-main-class ScheduleClass -entry:ScheduleClass \
	
java -Xmx400g -ea -cp ${MAINCP} disttaint.dt2Inst \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-duaverbose \
	-slicectxinsens \
   	-brinstr:off -duainstr:off  \
	-process-dir $subjectloc/java \
	-process-dir $subjectloc/4119 \
    -wrapTryCatch \
        -intraCD \
        -interCD \
        -exInterCD \
	-allowphantom \
	-serializeVTG \
	 1>out-DT2Instr/instr.out 2>out-DT2Instr/instr.err

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

