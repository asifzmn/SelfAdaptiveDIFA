out1=17
out2=16

ROOT=/home/xqfu
#MAINCP=".:/home/xqfu/thrift/bin"
for((i=$1;i<=$2;i++)); 
do   
	starttime=`date +%s%N | cut -b1-13`
	#timeout $outs java -cp ${MAINCP} XSocketClient3 /home/xqfuMessages/Message$i.txt
	timeout $out1 ./serverDT.sh  1>timecostServer_$i.log 2>&1 &
	sleep 1 
	timeout $out2 ./client3DT.sh /home/xqfu/fuzz/Equations/Equation$i.txt > timecostclientDT3_$i.log
	sleep 16
	rm /home/xqfu/thrift/test1/*.em
	mv /home/xqfu/thrift/test1/R*.*  /home/xqfu/thrift/test1/clientlog/$i
	stoptime=`date +%s%N | cut -b1-13`
	echo "client3 time:" `expr $stoptime - $starttime` >> timecostclientDT3_$i.log
done  
