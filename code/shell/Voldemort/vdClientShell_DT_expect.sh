#!/usr/bin/expect
set timeout 60
spawn ./vdClientShell_DT.sh test tcp://localhost:666
expect -re ">"
send "put \"hello\" \"world\" \n"
expect -re ">"
send "get \"hello\" \n"
expect -re "version(0:1): \"world\""
send "delete \"hello\" \n"
expect -re ">"
send "get \"hello\" \n"
expect -re "null"
send "exit \n"
sleep 10
expect eof
