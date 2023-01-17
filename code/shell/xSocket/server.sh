ROOT=/home/xqfu
MAINCP=".:/home/xqfu/xSocket/bin:/home/xqfu/xSocket/java/bin"
starttime=`date +%s%N | cut -b1-13`
java -cp ${MAINCP} XSocketServer
stoptime=`date +%s%N | cut -b1-13`
echo "StaticAnalysisTime for ${ver}${seed} elapsed: " `expr $stoptime - $starttime` milliseconds
