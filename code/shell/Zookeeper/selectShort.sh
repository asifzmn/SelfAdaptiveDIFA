#!/bin/bash

fn=$1
toremove=(107 67 334 221 130 189 245 268 355 244 354 243)
al=`cat $fn | wc -l`

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

for ((j=1;j<="$al";j++))
do
	isInList "$j"
	ret=$?
	if [ $ret -eq 0 ];
	then
		cat $fn | head -n$j | tail -n1
	fi
done


exit 0
