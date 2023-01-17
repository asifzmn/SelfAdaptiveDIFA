#!/usr/bin/expect
set timeout 60
spawn ./zkClient_Diver2.sh
sleep 1
send "\n"
expect -re "0]"
send "create -e /zk-temp 123\n"
expect -re "1]"
send "create -e /zk-temp2 456\n"
expect -re "2]"
send "ls /zk-temp\n"
expect -re "3]"
send "ls /zk-temp2\n"
expect -re "4]"
send "get /zk-temp\n"
expect -re "5]"
send "get /zk-temp2\n"
expect -re "6]"
send "set /zk-temp 789\n"
expect -re "7]"
send "set /zk-temp2 901\n"
expect -re "8]"
send "delete /zk-temp\n"
expect -re "9]"
send "delete /zk-temp2\n"
expect -re "10]"
send "quit\n"
expect eof