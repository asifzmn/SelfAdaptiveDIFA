#!/usr/bin/expect
set timeout 60
spawn ./client.sh
expect "connecting to"
sleep 1
for {set i 0} {$i<9999} {incr i 0}  {
	spawn ./client.sh
	
    set seconds [exec sh -c {./RANDOMNUM.sh}]
    puts "seconds: $seconds"
    sleep $seconds
	
	send "\003" 
}
expect eof
