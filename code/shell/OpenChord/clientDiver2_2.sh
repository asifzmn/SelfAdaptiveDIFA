ROOT=/home/xqfu
MAINCP=".:/home/xqfu/xSocket/DiverInstrumented2:/home/xqfu/libs/soot-trunk.jar:/home/xqfu/DUA1.jar:/home/xqfu/DiverThread.jar"
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} XSocketClient2
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds
