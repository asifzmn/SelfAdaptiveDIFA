#!/bin/bash

queryFile=sourceSinkStmtPair2.txt

source ./zk_global.sh


INDIR=$subjectloc
echo $INDIR
BINDIR=$subjectloc/OTBrPre
echo $BINDIR

cp $subjectloc/OT3Instrumented/staticVtg.dat $BINDIR
#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/mcia.jar:/home/xqfu/chord/lib:$INDIR:$BINDIR"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/FLOWDIST.jar:$INDIR:$BINDIR"

starttime=`date +%s%N | cut -b1-13`
	#"main,append" \
	#"append,ele" \
	#"add_process" \
	#-stmtcov
java -Xmx400g -ea -cp ${MAINCP} disttaint.OT3AnalysisAll \
	$queryFile \
	"$INDIR" \
	"$BINDIR" \
	"" \
	"" \
    "" \
    "" \ 
 
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0



