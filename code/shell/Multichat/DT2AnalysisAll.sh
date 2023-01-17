#!/bin/bash
source ./mc_global.sh

INDIR=$subjectloc
echo $INDIR
BINDIR=$subjectloc/DT2BrPre
echo $BINDIR
cp $subjectloc/DT2Instrumented/staticVtg.dat $BINDIR
MAINCP=".:/home/xqfu/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/FLOWDIST.jar:$INDIR:$BINDIR"

cp $subjectloc/stmtCoverage1.out $BINDIR
starttime0=`date +%s%N | cut -b1-13`

	starttime=`date +%s%N | cut -b1-13`
	query=sourceSinkStmtPairDiffClass2.txt
	 echo $query	
	java -Xmx400g -ea -cp ${MAINCP} disttaint.dt2AnalysisAll \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
	"-method" \
	"-stmtcov" \
	"-preprune" \
	"" \
	"" \
	
    stoptime=`date +%s%N | cut -b1-13`
	echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	

echo "Running finished."
stoptime0=`date +%s%N | cut -b1-13`
echo "All runTime elapsed: " `expr $stoptime0 - $starttime0` milliseconds	
exit 0

# hcai vim :set ts=4 tw=4 tws=4

