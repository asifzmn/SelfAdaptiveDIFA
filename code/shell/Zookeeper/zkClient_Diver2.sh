#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 [serverHost:port]"
	exit 1
fi

hostaddr=${1:-"127.0.0.1"}

source ./zk_global.sh

INDIR=$subjectloc/DiverInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DUA1.jar:$ROOT/DiverThread.jar:$INDIR"
#MAINCP=".:$ROOT/DiverThread.jar:$ROOT/banderaCommons.jar:$ROOT/banderaToolFramework.jar:$ROOT/commons-cli-1.3.1.jar:$ROOT/commons-io-1.4.jar:$ROOT/commons-lang-2.1.jar:$ROOT/commons-logging-1.2.jar:$ROOT/commons-pool-1.2.jar:$ROOT/trove-2.1.0.jar:$ROOT/xmlenc-0.52.jar:$ROOT/DUA1.jar:$ROOT/jibx-run-1.1.3.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DiverThread.jar:$INDIR"

for i in $subjectloc/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done

#for i in $subjectloc/svlib/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done

suffix="zk"

OUTDIR=Diveroutdyn2
mkdir -p $OUTDIR

ZOOMAIN="org.apache.zookeeper.ZooKeeperMain"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$hostaddr
java -Xmx40g -ea -DltsDebug=false -Dzookeeper.log.dir=. -Dzookeeper.root.logger=INFO,CONSOLE \
	-cp ${MAINCP} \
	${ZOOMAIN} \

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
