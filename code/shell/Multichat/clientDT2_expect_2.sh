#!/usr/bin/expect
set timeout 60
spawn ./clientDT2.sh
expect -re "Username"
send "user2\n"
expect -re "Password"
send "pass2\n"
expect -re "Login successful!"
send "hello2\n"
expect eof
