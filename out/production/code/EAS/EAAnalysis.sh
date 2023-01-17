#!/bin/bash

NT=${1:-""}

ROOT=$HOME
subjectloc=$HOME/workspace/Schedule/

INDIR=$subjectloc/EAoutdyn

MAINCP=".:/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.3.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/workspace/mcia/bin:$ROOT/tools/java_cup.jar:$ROOT/tools/DUAForensics-bins-code/DUAForensics:$ROOT/tools/DUAForensics-bins-code/LocalsBox:$ROOT/tools/DUAForensics-bins-code/InstrReporters:$subjectloc/lib:$INDIR"

starttime=`date +%s%N | cut -b1-13`
	#"main,append" \
	#"append,ele" \
	#"add_process" \
java -Xmx2800m -ea -cp ${MAINCP} EAS.EAAnalysis \
	"quantum" \
	"$INDIR" \
	$NT

stoptime=`date +%s%N | cut -b1-13`
echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0

