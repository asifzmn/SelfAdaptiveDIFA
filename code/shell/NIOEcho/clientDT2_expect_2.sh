#!/usr/bin/expect
set timeout 60
spawn ./clientDT2.sh
sleep 1
send "hello2\n"
expect eof
