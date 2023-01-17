#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh
ROOT=/home/xqfu/
#DRIVERCLASS=ChatServer.core.MainServer
subjectloc=$subjectloc/


#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/mcia8.jar:$$subjectloc/dist/classes"
MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/DUA1.jar:/home/xqfu/FLOWDIST.jar"
echo $MAINCP

rm -R out-DTInstr -f
mkdir -p out-DTInstr

#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/workspace/DUAForensics/bin:/home/xqfu/workspace/LocalsBox/bin:/home/xqfu/workspace/InstrReporters/bin:/home/xqfu/workspace/mcia/bin":$subjectloc/dist/classes/${ver}${seed}:$subjectloc/lib
#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/libs/soot-trunk4.jar:/home/xqfu/DistEA/DUA1.jar:/home/xqfu/Diver/LocalsBox:/home/xqfu/Diver/InstrReporters:$subjectloc/dist/classes/${ver}${seed}:/home/xqfu/Diver/Diver.jar:$subjectloc/lib"
#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/libs/DUA1.jar:$subjectloc/dist/classes/${ver}${seed}:/home/xqfu/libs/mcia.jar:$subjectloc/lib:/home/xqfu/mcia/bin"

#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/dist/classes:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/mcia8.jar"

SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/dist/testclasses:/home/xqfu/FLOWDIST.jar"


echo $SOOTCP
OUTDIR=$subjectloc/DTInstrumented/testclasses
# rm -R $OUTDIR -f
# mkdir -p $OUTDIR
starttime=`date +%s%N | cut -b1-13`
	#-sclinit \
	#-wrapTryCatch \
	#-debug \
	#-dumpJimple \:wq

	#-statUncaught \
	#-ignoreRTECD \
	#-exInterCD \
	#-main-class ScheduleClass -entry:ScheduleClass \
java -Xmx400g -ea -cp ${MAINCP} disttaint.dtInst \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-duaverbose \
	-slicectxinsens \
   	-brinstr:off -duainstr:off  \
	-process-dir $subjectloc/dist/testclasses \
	-allowphantom \
	 1>out-DTInstr/instr.out 2>out-DTInstr/instr.err

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

