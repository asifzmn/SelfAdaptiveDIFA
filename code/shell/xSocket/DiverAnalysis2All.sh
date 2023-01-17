#!/bin/bash
source ./xs_global.sh

INDIR=$subjectloc/Diveroutdyn
echo $INDIR
BINDIR=$subjectloc/DiverInstrumented
echo $BINDIR

cp /home/xqfu/nioecho/stmtCoverage1.out $INDIR
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/distEADIVER/bin:$INDIR:$BINDIR"



cat methods.txt | while read LINE
do
     echo "WORK Method\n"
     echo $LINE
#	query=\$\{1:-\"<\"$LINE\">\"\}
	starttime=`date +%s%N | cut -b1-13`
	query=$LINE
	 echo $query	
	java -Xmx9200m -ea -cp ${MAINCP} Diver.DiverAnalysisStmt \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
    2	
	
	stoptime=`date +%s%N | cut -b1-13`
	echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	
done



echo "Running finished."

exit 0

# hcai vim :set ts=4 tw=4 tws=4

