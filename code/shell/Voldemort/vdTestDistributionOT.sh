#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh

# INDIR=$subjectloc/DTInstrumented
#INDIR=$subjectloc/dist/testclasses/:$subjectloc/dist/classes


INDIR=$subjectloc/OTInstrumented

#MAINCP="/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$ROOT/workspace/DUAForensics/bin:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk.jar:$ROOT/DUA1.jar:$ROOT/DUAForensics-bins-code/InstrReporters:$ROOT/DUAForensics-bins-code/LocalsBox:$ROOT/FLOWDIST.jar:$INDIR:$subjectloc/conf"
for i in $subjectloc/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done

suffix="vd"

MAINCLS="voldemort.partition.TestDistribution"

starttime=`date +%s%N | cut -b1-13`


	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$subjectloc/config/two_node_cluster/node_1/config/cluster.xml \
java -Xmx40g -ea -DltsDebug=true \
	-cp ${MAINCP} \
	${MAINCLS} \
	$subjectloc/config/test_config2/config/cluster.xml \
	4 \
	1

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
