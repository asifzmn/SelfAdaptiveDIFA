#!/usr/bin/expect
set timeout 60
spawn ./clientDT2.sh
sleep 1
send "hello1\n"
expect eof
