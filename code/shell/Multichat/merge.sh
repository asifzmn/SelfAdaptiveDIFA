#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./mc_global.sh
ROOT=/home/xqfu/
#DRIVERCLASS=ChatServer.core.MainServer
subjectloc=/home/xqfu/multichat/


#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/mcia8.jar:$/home/xqfu/multichat/bin"
MAINCP="/home/xqfu/libs/distDIVER.jar"
echo $MAINCP

starttime=`date +%s%N | cut -b1-13`
	#-sclinit \
	#-wrapTryCatch \
	#-debug \
	#-dumpJimple \
	#-statUncaught \
	#-ignoreRTECD \
	#-exInterCD \
	#-main-class ScheduleClass -entry:ScheduleClass \
java   -cp ${MAINCP} merge.mergedistDIVER DA23.out distEA2.out all.out 

stoptime=`date +%s%N | cut -b1-13`
echo "for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

