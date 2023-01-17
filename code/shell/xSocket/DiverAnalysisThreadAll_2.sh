#!/bin/bash
source ./xs_global.sh

INDIR=/home/xqfu/xSocket/Diveroutdyn2
echo $INDIR
BINDIR=/home/xqfu/xSocket/DiverInstrumented2
echo $BINDIR
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DiverThread.jar:$INDIR:$BINDIR"


#	query=\$\{1:-\"<\"$LINE\">\"\}
	starttime=`date +%s%N | cut -b1-13`
	query=methods_2.txt
	 echo $query	
	java -Xmx92000m -ea -cp ${MAINCP} Diver.DiverAnalysisAll \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
	4  \
	"-stmtcov" \
	"-postprune" \
	
	stoptime=`date +%s%N | cut -b1-13`
	echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	



echo "Running finished."

exit 0

# hcai vim :set ts=4 tw=4 tws=4

