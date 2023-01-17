#!/bin/bash

#query=${4:-"quantum_expire"}
query=${1:-"<ChatServer.handler.ClientInfo: void setSocket(java.net.Socket)> - r0 := @this: ChatServer.handler.ClientInfo; <ChatServer.core.MainServer: void main(java.lang.String[])> - r0 := @parameter0: java.lang.String[]"}
source ./mc_global.sh

#INDIR=/home/xqfu/multichat
INDIR=/home/xqfu/multichat
echo $INDIR
BINDIR=/home/xqfu/multichat/DT2Instrumented
echo $BINDIR

#MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/libs/mcia.jar:$subjectloc/lib:$INDIR:$BINDIR"
MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar::/home/xqfu/libs/DUA1.jar:/home/xqfu/libs/soot-trunk.jar:$ROOT/FLOWDIST.jar:$INDIR:$BINDIR"

starttime=`date +%s%N | cut -b1-13`
	#"main,append" \
	#"append,ele" \
	#"add_process" \
	#-stmtcov
java -Xmx900m -ea -cp ${MAINCP} disttaint.dt2Analysis \
	"$query" \
	"$INDIR" \
	"$BINDIR" \
	"" \
	"" \
	"" \
	"-debug" \
	"" \
	"" \

stoptime=`date +%s%N | cut -b1-13`
echo "RunTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."

exit 0


# hcai vim :set ts=4 tw=4 tws=4

