#!/usr/bin/expect
set timeout 60
spawn ./clientODD.sh
sleep 3  
send "User \r"
expect "Password:"
send "Pass \r"
expect "Login successful!"
sleep 1
    set sentence [exec sh -c {./RandomSentence.sh}]
    puts "sentence: $sentence"
    send "$sentence \r"
    send "\003"
expect eof
