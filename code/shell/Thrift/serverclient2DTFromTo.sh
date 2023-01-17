out1=24
out2=20

ROOT=/home/xqfu
for((i=$1;i<=$2;i++)); 
do   
	starttime=`date +%s%N | cut -b1-13`
	timeout $out1 ./serverDT.sh  1>timecostServer_$i.log 2>&1 &
	sleep 1 
	timeout $out2 ./client2DT.sh  > timecostclientDT2_$i.log
	sleep 8
	rm /home/xqfu/thrift/test1/*.em 
	mv /home/xqfu/thrift/test1/R*.txt  /home/xqfu/thrift/test1/clientlog/$i
	stoptime=`date +%s%N | cut -b1-13`
	echo "client3 time:" `expr $stoptime - $starttime` >> timecostclientDT2_$i.log
done  
