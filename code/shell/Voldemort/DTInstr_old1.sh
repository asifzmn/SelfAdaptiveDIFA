#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh
ROOT=/home/xqfu
subjectloc=$ROOT/voldemort

rm -R $subjectloc/DTInstrumented -f
mkdir $subjectloc/DTInstrumented
cp $subjectloc/dist/resources $subjectloc/DTInstrumented -R

mkdir $subjectloc/DTInstrumented/classes
cp $subjectloc/dist/classes/log4j-admin.properties $subjectloc/DTInstrumented/classes

mkdir $subjectloc/DTInstrumented/testclasses
cp $subjectloc/dist/testclasses/*.sql $subjectloc/DTInstrumented/testclasses
cp $subjectloc/dist/testclasses/coordinator $subjectloc/DTInstrumented/testclasses

starttime=`date +%s%N | cut -b1-13`
./DTInstr_1.sh
./DTInstr_2.sh
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	
