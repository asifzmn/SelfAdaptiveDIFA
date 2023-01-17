#!/bin/bash

fn=$1
#toremove=(97 372 426 427 616 242 295 401 607 608 609 468 469)
#toremove=(107 112 137 223 228 35 36 383 384 402 432 435 447 45 476 481 483 500 581 583)
toremove=()
#al=`cat $fn | wc -l`
#al=721
#al=708
al=688
ORGDIR=`pwd`/distEAoutdyn.workingunit
NEWDIR=`pwd`/distEAoutdynComm

function isInList()
{
	t=$1
	for ((i=0;i<${#toremove[@]};i++));
	do
		if [ ${toremove[$i]} == "$t" ];then
			return 1
		fi
	done
	return 0 
}


mkdir -p $NEWDIR 

rn=0
for ((j=1;j<="$al";j++))
do
	isInList "$j"
	ret=$?
	if [ $ret -eq 0 ];
	then
		#cat $fn | head -n$j | tail -n1
		ns=`cat $ORGDIR/test$j/run.out | grep -a -c -E "clock sent ="`
		nr=`cat $ORGDIR/test$j/run.out | grep -a -c -E "clock received ="`

		if [ $ns -ge 1 -a $nr -ge 1 ];
		then
			((rn++))
			#cp -r $ORGDIR/test$j $NEWDIR/test$rn
			#echo "unit test $j is picked."
			cat $fn | head -n$j | tail -n1
		fi
	#else
		#echo -e "\ttest $j is not a unit test, skipped."
	fi
done
echo "totally $rn unit tests involve multi-component communication thus are chosen and copied to $NEWDIR"

exit 0
