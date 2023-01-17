#!/bin/bash

fn=$1
toremove=()
#al=`cat $fn | wc -l`
al=309
ORGDIR=`pwd`/distEAoutdyn
NEWDIR=`pwd`/distEAoutdynUnit

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

rn=1
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
			cat $fn | head -n$j | tail -n1
			cp -r $ORGDIR/test$j $NEWDIR/test$rn
			((rn++))
			#echo "unit test $j is picked."
		fi
	#else
		#echo -e "\ttest $j is not a unit test, skipped."
	fi
done
echo "totally $rn unit tests involve multi-component communication thus are chosen and copied to $NEWDIR"

exit 0
