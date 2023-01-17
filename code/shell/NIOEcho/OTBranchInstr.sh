#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./ne_global.sh
ROOT=/home/xqfu/
#DRIVERCLASS=C
#subjectloc=/home/xqfu/TEST/


MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/FLOWDIST.jar"
echo $MAINCP
rm entitystmt.out.branch
rm -R out-OTBrPre -f
mkdir -p out-OTBrPre


SOOTCP=".:$subjectloc/bin:/home/xqfu/FLOWDIST.jar"
#SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/nioecho/bin:/home/xqfu/tools/DUAForensics-bins-code/LocalsBox"


echo $SOOTCP
OUTDIR=$subjectloc/OTBrPre
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
java -Xmx100g -ea -cp ${MAINCP} disttaint.OTBranchInst \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false  \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
	-process-dir $subjectloc/bin \
     -duaverbose \
	-slicectxinsens \
	-wrapTryCatch \
        -intraCD \
        -interCD \
        -exInterCD \
	-allowphantom \
	-dumpJimple \
	-allowphantom \
#    1>out-OTBrPre/bpre.out 2>out-OTBrPre/bpre.err

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds
cp $subjectloc/OTBrPre/*.branch .
cp $subjectloc/OTBrPre/stmtids.out .
echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

