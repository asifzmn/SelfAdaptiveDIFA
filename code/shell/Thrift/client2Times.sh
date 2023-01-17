% outs=10

ROOT=/home/xqfu
#MAINCP=".:/home/xqfu/netty/java:/home/xqfu/netty/netty-all-4.1.19.Final.jar"
for((i=1;i<=$1;i++)); 
do  	
	starttime=`date +%s%N | cut -b1-13`
	#timeout $outs java -cp ${MAINCP} XSocketClient3 /home/xqfu/fuzz/Messages/Message$i.txt
	#timeout $outs ./client2.sh /home/xqfu/fuzz/Messages/Message$i.txt > timecostclient2_$i.log
	./client2.sh  > timecostclient2_$i.log
	stoptime=`date +%s%N | cut -b1-13`
	echo "client3 time:" `expr $stoptime - $starttime` >> timecostclient2_$i.log
done  
