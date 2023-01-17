#!/bin/bash
source ./chord_global.sh

INDIR=$subjectloc
echo $INDIR
BINDIR=$subjectloc
echo $BINDIR
cp $subjectloc/OT3Instrumented/staticVtg.dat $BINDIR

MAINCP=".:/home/xqfu/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/FLOWDIST.jar:$INDIR:$BINDIR"

cp $subjectloc/OT3Instrumented/stmtC*1.out $BINDIR
starttime0=`date +%s%N | cut -b1-13`

	query=sourceSinkStmtPair2.txt
	 echo $query	
	java -Xmx100g -ea -cp ${MAINCP} disttaint.OT3AnalysisAll \
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

