#!/bin/bash
#while true
#do
#    sleep1=$(($RANDOM%3+1))
#echo "sleep1="$sleep1
    sleep2=$(($RANDOM%9+14))
echo "sleep2="$sleep2
    var1=$(./RandomSentence.sh)
#echo $var1	
    #expect -c "spawn ./client.sh; sleep $sleep1; send "$var1 \r"; send "\003\r"; sleep 1"
	# sleep $(($RANDOM%3+1))
	/usr/bin/expect <<EOF
	set timeout 2  
	spawn ./client.sh
	expect -re "Username"
	send "user2\n"
	expect -re "Password"
	send "pass2\n"
	expect -re "Login successful!"
	send "Client2: $var1 \r"
	sleep $sleep2
expect eof
EOF

