#!/bin/bash


source ./th_global.sh

INDIR=$subjectloc/DADSInstrumented/
cp $INDIR/functionList.out  .
MAINCP=".:$ROOT/DADS.jar"
rm -R allResult.txt -f
    #"queries.lst"
starttime=`date +%s%N | cut -b1-13`
java -Xmx10g -ea -cp ${MAINCP} \
	Dads.DadsQueryClient \
	localhost \
 2000, \
	
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4

