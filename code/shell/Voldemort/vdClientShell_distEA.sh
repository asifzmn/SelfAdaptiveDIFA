#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh

INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/distEAInstrumented.syncnio.thread
#INDIR=$subjectloc/dist/classes/:$subjectloc/dist/testclasses

#MAINCP="/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$ROOT/workspace/DUAForensics/bin:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"
#MAINCP="$ROOT/workspace/mcia/bin:$INDIR:$subjectloc/conf"

MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/DUA1.jar:$ROOT/libs/DistEA6.jar:$ROOT/libs/soot-trunk.jar:$INDIR"

#for i in /home/xqfu/libs/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done
for file in /home/xqfu/voldemort/dist/*.jar;
do
  MAINCP=$MAINCP:$file
done

for file in /home/xqfu/voldemort/lib/*.jar;
do
  MAINCP=$MAINCP:$file
done

for file in /home/xqfu/voldemort/contrib/*/libs/*.jar;
do
  MAINCP=$MAINCP:$file
done

MAINCP=$MAINCP:/home/xqfu/voldemort/dist/resources

#for i in $subjectloc/svlib/*.jar;
#do
#	MAINCP=$MAINCP:$i
#done

suffix="vd"

OUTDIR=distEAoutdyn
mkdir -p $OUTDIR

VDMAIN="jline.ConsoleRunner"

starttime=`date +%s%N | cut -b1-13`

	#-Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.local.only=false \
	#$subjectloc/config/test_config1/config/
	#tcp://cse-rsws-06.cse.nd.edu:6667
	#"tcp://localhost:6666"
	#-server -Dcom.sun.management.jmxremote 
#java -Xmx2G -ea -DltsDebug=true -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.properties \
java -Xmx2G -ea -DltsDebug=false -DuseToken=false -Dlog4j.configuration=file://$subjectloc/src/java/log4j.properties \
	-cp ${MAINCP} \
	${VDMAIN} \
	voldemort.VoldemortClientShell \
	"test" \
	tcp://localhost:6666

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

