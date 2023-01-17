#!/bin/bash
#while true
#do
    sleep1=$(($RANDOM%3+1))
echo $sleep1
    var1=$(./RandomSentence.sh)
echo $var1	
    #expect -c "spawn ./client.sh; sleep $sleep1; send "$var1 \r"; send "\003\r"; sleep 1"
	# sleep $(($RANDOM%3+1))
	/usr/bin/expect <<EOF
	set timeout 2  
	spawn ./clientODD.sh
    sleep 1
    sleep $sleep1
	send "$var1 \r"
	sleep 1
expect eof
EOF

