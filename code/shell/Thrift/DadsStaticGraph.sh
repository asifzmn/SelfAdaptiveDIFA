#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./th_global.sh
ROOT=/home/xqfu/
#DRIVERCLASS=ChatServer.core.MainServer
#subjectloc=$subjectloc


# MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/FLOWDIST.jar"
MAINCP=".:$ROOT/DADS.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:/home/xqfu/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar"
echo $MAINCP

rm -R out-ODDInstr -f
mkdir -p out-ODDInstr



SOOTCP=".:$subjectloc/java/bin:$subjectloc/0110/lib/java/build:$ROOT/DADS.jar"


echo $SOOTCP
OUTDIR=$subjectloc/ODDStaticGraph
# OUTDIR=$subjectloc/DADSInstrumented
# rm -R $OUTDIR -f
# mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`
	#-sclinit \
	#-wrapTryCatch \
	#-debug \
	#-dumpJimple \
	#-statUncaught \
	#-ignoreRTECD \
	#-exInterCD \
	#-main-class ScheduleClass -entry:ScheduleClass \
	
java -Xmx400g -ea -cp ${MAINCP} Dads.DadsStaticGraph \
	-w -cp ${SOOTCP} \
	-p cg verbose:false,implicit-entry:false -p cg.spark verbose:false,on-fly-cg:true,rta:false \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-duaverbose \
	-slicectxinsens \
   	-brinstr:off -duainstr:off  \
        -process-dir $subjectloc/0110/lib/java/build  \
        -process-dir $subjectloc/java/bin  \
    -wrapTryCatch \
        -intraCD \
        -interCD \
        -exInterCD \
	-allowphantom \
	-serializeVTG \
#	 1>out-ODDInstr/staticgraph.out 2>out-ODDInstr/staticgraph.err

cp $subjectloc/staticVtg.dat $subjectloc/staticVtg.dat
cp $subjectloc/staticVtg.dat $subjectloc/DADSInstrumented/staticVtg.dat
# rm *.class
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0



