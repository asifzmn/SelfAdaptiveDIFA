#!/usr/bin/expect
set timeout 60
spawn ./clientDT2.sh
expect -re "Username"
send "user1\n"
expect -re "Password"
send "pass1\n"
expect -re "Login successful!"
send "hello1\n"
expect eof
