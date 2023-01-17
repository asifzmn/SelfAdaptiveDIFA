#!/bin/bash
source ./chord_global.sh

INDIR=$subjectloc
echo $INDIR
BINDIR=$subjectloc/DTInstrumented
echo $BINDIR
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:$ROOT/FLOWDIST.jar:$INDIR:$BINDIR"

rm $subjectloc/methodsInPair.out -f
rm $subjectloc/methodList.out -f
starttime0=`date +%s%N | cut -b1-13`
	#"main,append" \
	#"append,ele" \
	#"add_process" \
	#-stmtcov
cat sourceSinkMethodPairDiffClass.txt | while read LINE
do
     # echo "WORK Method\n"
     # echo $LINE
#	query=\$\{1:-\"<\"$LINE\">\"\}

  starttime=`date +%s%N | cut -b1-13`
	query=$LINE
	 # echo $query	
  java -Xmx10g -ea -cp ${MAINCP} disttaint.dtAnalysis \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
   4 	

  stoptime=`date +%s%N | cut -b1-13`
  echo "RunTime for  elapsed: " `expr $stoptime - $starttime` milliseconds
done

#stoptime=`date +%s%N | cut -b1-13`
#echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

  stoptime0=`date +%s%N | cut -b1-13`
  echo "RunTime  elapsed: " `expr $stoptime0 - $starttime0` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4

