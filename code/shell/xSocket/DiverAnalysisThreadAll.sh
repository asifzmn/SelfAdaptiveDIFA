#!/bin/bash
#source ./kf_global.sh

INDIR=/home/xqfu/xSocket/Diveroutdyn2
echo $INDIR
BINDIR=/home/xqfu/xSocket/DiverInstrumented2
echo $BINDIR
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/DiverThread.jar:$INDIR:$BINDIR"



cat methods.txt | while read LINE
do
     echo "WORK Method\n"
     echo $LINE
#	query=\$\{1:-\"<\"$LINE\">\"\}
	starttime=`date +%s%N | cut -b1-13`
	query=$LINE
	 echo $query	
	java -Xmx92000m -ea -cp ${MAINCP} Diver.DiverAnalysis \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
        4	
	
	stoptime=`date +%s%N | cut -b1-13`
	echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	
done



echo "Running finished."

exit 0

# hcai vim :set ts=4 tw=4 tws=4

