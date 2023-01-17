#!/bin/bash

ROOT=$HOME
subjectloc=$HOME/workspace/Schedule/

MAINCP=".:/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.3.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/DUAForensics-bins-code/DUAForensics:$ROOT/tools/DUAForensics-bins-code/LocalsBox:$ROOT/tools/DUAForensics-bins-code/InstrReporters:$ROOT/workspace/mcia/bin:$ROOT/tools/java_cup.jar"

mkdir -p out-EAInstr

SOOTCP=".:$ROOT/software/j2re1.4.2_18/lib/rt.jar:$ROOT/tools/DUAForensics-bins-code/DUAForensics:$ROOT/tools/DUAForensics-bins-code/LocalsBox:$ROOT/tools/DUAForensics-bins-code/InstrReporters:$ROOT/workspace/mcia/bin:$subjectloc/bin:$subjectloc/lib"

OUTDIR=$subjectloc/EAInstrumented
mkdir -p $OUTDIR

starttime=`date +%s%N | cut -b1-13`
java -Xmx1600m -ea -cp ${MAINCP} EAS.EAInst \
	-w -cp ${SOOTCP} \
	-p cg verbose:true,implicit-entry:false -p cg.spark verbose:true,on-fly-cg:true,rta:true \
	-f c -d "$OUTDIR" -brinstr:off -duainstr:off \
   	-duaverbose \
	-slicectxinsens \
	-debug \
	-dumpJimple \
	-allowphantom \
	-main-class ScheduleClass -entry:ScheduleClass \
	-process-dir $subjectloc/bin/${ver}${seed}  \
	1>out-EAInstr/instr.out 2>out-EAInstr/instr.err
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0

