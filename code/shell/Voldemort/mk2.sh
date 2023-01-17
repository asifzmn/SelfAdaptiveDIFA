#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0 "
	exit 1
fi

source ./vd_global.sh
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/libs/soot-trunk6.jar:$ROOT/libs/DUA1.jar:$ROOT/libs/mcia6.jar:$subjectloc:$subjectloc/conf:$subjectloc/bin"
#MAINCP=".:$ROOT/tools/j2sdk1.4.2_18/jre/lib/rt.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.5.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/workspace/DUAForensics2.5/bin:$ROOT/workspace/mcia/bin:$ROOT/tools/java_cup.jar"
#MAINCP=".:$ROOT/tools/j2sdk1.4.2_18/jre/lib/rt.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-trunk.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/workspace/DUAForensics2.5/bin:$ROOT/workspace/mcia/bin:$ROOT/tools/java_cup.jar"

#SOOTCP=".:/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/workspace/LocalsBox/bin:$ROOT/workspace/InstrReporters/bin:$ROOT/workspace/DUAForensics2.5/bin:$ROOT/workspace/mcia/bin:$subjectloc/dist/classes:$subjectloc/dist/testclasses:$subjectloc/lib/netty-3.5.8.Final.jar"
SOOTCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$subjectloc/bin:$subjectloc/lib/netty-3.7.0.Final.jar:$subjectloc/dist/classes:$subjectloc/dist/testclasses:$subjectloc/bin"

#for i in $subjectloc/lib/*.jar;
#do
#	SOOTCP=$SOOTCP:$i
#done

suffix="vd"

LOGDIR=out-distEAInstr
mkdir -p $LOGDIR
logout=$LOGDIR/instr-$suffix.out
logerr=$LOGDIR/instr-$suffix.err

OUTDIR=$subjectloc/distEAInstrumented
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`

	#-allowphantom \
   	#-duaverbose \
	#-wrapTryCatch \
	#-dumpJimple \
	#-statUncaught \
	#-perthread \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \
	#-syncnio \
	#-main-class $DRIVERCLASS \
	#-entry:$DRIVERCLASS \

stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds

#echo "Instrumentation done, now copying resources required for running."
mkdir -p $OUTDIR/voldemort/xml/
mkdir -p  $OUTDIR/voldemort/utils/ 
mkdir -p  $OUTDIR/voldemort/utils/
mkdir -p  $OUTDIR/voldemort/server/
mkdir -p  $OUTDIR/voldemort/server/http/
mkdir -p  $OUTDIR/voldemort/server/http/utils

cp -r $subjectloc/dist/classes/voldemort/xml/*.xsd $OUTDIR/voldemort/xml/

cp -r $subjectloc/dist/testclasses/coordinator $OUTDIR/
cp -r $subjectloc/dist/testclasses/mysql_test_init.sql $OUTDIR/
cp -r $subjectloc/dist/testclasses/voldemort/config $OUTDIR/voldemort/
cp -r $subjectloc/dist/testclasses/voldemort/utils/Xtranslcl.c.input $OUTDIR/voldemort/utils/
cp -r $subjectloc/dist/classes/log4j-admin.properties $OUTDIR/ 
cp -r $subjectloc/dist/classes/voldemort/server/http/gui/templates $OUTDIR/voldemort/server/http/gui/

echo "Running finished."
exit 0


# hcai vim :set ts=4 tw=4 tws=4

