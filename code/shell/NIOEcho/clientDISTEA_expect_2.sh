#!/usr/bin/expect
set timeout 60
spawn ./clientDISTEA.sh
sleep 1
send "hello1\n"
expect eof
