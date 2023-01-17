#!/usr/bin/expect
set timeout 60
spawn ./serverDT2.sh
expect -re "oc >"
send "joinN -port 8080 -bootstrap 10.99.1.191:4242\n"
expect -re "oc >"
send "entriesN\n"
send \003
expect eof
