#!/usr/bin/expect
set timeout 60
spawn ./clientDIVER2.sh
sleep 1
send "hello1\n"
expect eof
