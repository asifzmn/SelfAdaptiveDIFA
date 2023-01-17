#!/bin/bash


source ./ne_global.sh

INDIR=$subjectloc/ODDInstrumented/

MAINCP=".:$ROOT/DistODD.jar:/home/xqfu/DUA1.jar:$ROOT/libs/soot-trunk.jar:$INDIR"
cp $INDIR/staticVtg.dat .

    #"queries.lst"
starttime=`date +%s%N | cut -b1-13`
java -Xmx10g -ea -cp ${MAINCP} \
	ODD.ODDQueryServer \
	
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4

