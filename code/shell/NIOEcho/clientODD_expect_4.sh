#!/usr/bin/expect
set timeout 60
spawn ./clientODD.sh
expect "connecting to"
sleep 1    
set sentence [exec sh -c {./RandomSentence.sh}]
    puts "sentence: $sentence"
    send "$sentence \r"
    send "\003"
expect eof
