#!/usr/bin/expect
set timeout 60
spawn ./clientDIVER2.sh
sleep 1
send "hello2\n"
expect eof
