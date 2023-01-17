#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./rk_global.sh
#ROOT=/home/xqfu
#DRIVERCLASS=ChatServer.core.MainServer
#subjectloc=/home/xqfu/nioecho/


#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk.jar:$ROOT/libs/DUA1.jar:$ROOT/libs/mcia8.jar:$$ROOT/nioecho/bin"
MAINCP=".:$ROOT/libs/soot-trunk.jar:$ROOT/DUA7.jar:$ROOT/FLOWDIST.jar"
echo $MAINCP
rm -R out-OTInstr -f
mkdir -p out-OTInstr


SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/bin:$ROOT/FLOWDIST.jar"


echo $SOOTCP
OUTDIR=$subjectloc/OTInstrumented
rm -R $OUTDIR
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

java -Xmx100g -ea -cp ${MAINCP} disttaint.OTInstr \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-brinstr:off -duainstr:off  \
	-process-dir $subjectloc/bin \
	-allowphantom \
#	 1>out-OTInstr/instr.out 2>out-OTInstr/instr.err

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

