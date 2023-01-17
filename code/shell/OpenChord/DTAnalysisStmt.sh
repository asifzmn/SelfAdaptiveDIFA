#!/bin/bash

#query=${4:-"quantum_expire"}
query=${1:-"<C: void main(java.lang.String[])> - virtualinvoke r2.<B: void printString(int,java.lang.String,java.lang.String)>(-1, \"positive\", \"negative\");<B: void printString(int,java.lang.String,java.lang.String)> - r1 := @parameter1: java.lang.String"}

source ./t_global.sh

#INDIR=/home/xqfu/multichat
INDIR=/home/xqfu/TEST
echo $INDIR
BINDIR=/home/xqfu/TEST/DTInstrumented
echo $BINDIR

#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/mcia.jar:/home/xqfu/chord/lib:$INDIR:$BINDIR"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/FLOWDIST.jar:$INDIR:$BINDIR"

starttime=`date +%s%N | cut -b1-13`
	#"main,append" \
	#"append,ele" \
	#"add_process" \
	#-stmtcov
java -Xmx900m -ea -cp ${MAINCP} disttaint.dtAnalysisStmt \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
	"-debug" \
	"" \
        ""
 
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4

