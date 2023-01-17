#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh
ROOT=/home/xqfu
#subjectloc=/home/xqfu/TEST
#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/DT2BrPre/
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$INDIR:$ROOT/DUA1.jar:$subjectloc/dist/voldemort-contrib-1.10.26.jar"
#for file in /home/xqfu/voldemort/dist/*.jar;
#do
#  MAINCP=$MAINCP:$file
#done

for file in /home/xqfu/voldemort/lib/*.jar;
do
  MAINCP=$MAINCP:$file
done

for file in /home/xqfu/voldemort/contrib/*/libs/*.jar;
do
  MAINCP=$MAINCP:$file
done

MAINCP=$MAINCP:/home/xqfu/voldemort/dist/resources

suffix="vd"

OUTDIR=$subjectloc/DT2Broutdyn/
mkdir -p $OUTDIR

VDMAIN="voldemort.server.VoldemortServer"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$subjectloc/config/test_config1/config/
	#-Dcom.sun.management.jmxremote -server 
	#$subjectloc/config/single_node_cluster/config
	#$subjectloc/config/test_config2/config/
	#$subjectloc/config/test_config1/config
#jdb -Xmx2G -DltsDebug=true -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.properties \
#	-classpath ${MAINCP} \
java -Xmx20G -ea -DltsDebug=true -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.properties \
	-cp ${MAINCP} \
	${VDMAIN} \
	$subjectloc \
	$subjectloc/config/single_node_rest_server/config

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0
