#!/usr/bin/expect
set timeout 60
spawn ./serverDIVER2.sh
expect -re "oc >"
send "joinN -port 8081 -bootstrap 10.99.1.191:4242\n"
expect -re "oc >"
send "insertN -key test -value test\n"
send \003
expect eof
