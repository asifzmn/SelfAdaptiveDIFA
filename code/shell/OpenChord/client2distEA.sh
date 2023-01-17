ROOT=/home/xqfu
#MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/libs/DistEA6.jar:/home/xqfu/xSocket/distEAInstrumented"
MAINCP=".:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/xSocket/distEAInstrumented:/home/xqfu/libs/DistEA8.jar"
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} -DltsDebug=true -DtrackSender=true XSocketClient2
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds
