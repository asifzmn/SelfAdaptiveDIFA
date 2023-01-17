#!/usr/bin/expect
set timeout 60
spawn ./zkGenerateLoad.sh
sleep 1
for {set i 0} {$i<9999} {incr i 0}  {
	expect "Connected to Socket"
	sleep 1
    expect "Connected to Socket"
	sleep 50
    set seconds [exec sh -c {./RANDOMNUM.sh}]
    sleep $seconds
	send "\003 /r"
	send "\003"
    sleep 10
	send "\003 /r"
	send "\003 "
	sleep 30
    spawn ./zkGenerateLoad.sh
	sleep 1
}
expect eof
