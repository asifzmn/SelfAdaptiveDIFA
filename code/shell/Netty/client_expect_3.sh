#!/usr/bin/expect
set timeout 60
spawn ./client.sh
sleep 3
for {set i 0} {$i<9999} {incr i 0}  {
    set sentence [exec sh -c {./RandomSentence.sh}]
    puts "sentence: $sentence"
    send "$sentence \r"
	
    set seconds [exec sh -c {./RANDOMNUM.sh}]
    puts "seconds: $seconds"
    sleep $seconds
}
expect eof
