#!/usr/bin/expect
set timeout 60
spawn ./clientODD.sh
expect "connecting to"
sleep 1
while true
do    
    set sentence [exec sh -c {./RandomSentence.sh}]
    puts "sentence: $sentence"
    send "$sentence \r"

    set seconds [exec sh -c {./RANDOMNUM.sh}]
    puts "seconds: $seconds"
    expect "received data from "
    sleep $seconds
done
expect eof
