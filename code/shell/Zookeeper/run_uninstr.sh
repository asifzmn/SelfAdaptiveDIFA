#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0"
	exit 1
fi

source ./zk_global.sh

MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/DistEA/DUA1.jar:$ROOT/DistEA/DistEA.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/conf"

for i in $subjectloc/lib/*.jar;
do
	MAINCP=$MAINCP:$i
done
for i in $subjectloc/svlib/*.jar;
do
	MAINCP=$MAINCP:$i
done

OUTDIR=Runout-uninstr
mkdir -p $OUTDIR

FAILLIST=./errorTestName.txt
PASSLIST=./normalTestName.txt
> $FAILLIST
> $PASSLIST

function RunAllInOne()
{
	java -Xmx4000m -ea -cp $MAINCP  $DRIVERCLASS 
}

function RunAllClasses()
{
	java -Xmx4000m -ea -cp $MAINCP  $RUNALLCLASS
}

function RunPerClass()
{
	# to run a single test at a time with each test class as a single test
	local i=0
	cat $subjectloc/inputs/testinputs.cls.txt | dos2unix | \
	while read testname;
	do
		let i=i+1

		echo "Run Test #$i" # [" $testname "] ....."
		java -Xmx4000m -ea -cp $MAINCP  $RUNPERCLASS $testname #1> $OUTDIR/$i.out 2> $OUTDIR/$i.err
		if [ -s $OUTDIR/$i.err ];then
			echo "$testname" >> $FAILLIST.cls
		else
			echo "$testname" >> $PASSLIST.cls
		fi
	done
}

function RunOneByOne()
{
	# to run a single test at a time with each test method as a single test
	local i=0
	cat $subjectloc/inputs/testinputs.txt | dos2unix | \
	while read testname;
	do
		let i=i+1

		echo "Run Test #$i" # [" $testname "] ....."
		java -Xmx4000m -ea -cp $MAINCP  $DRIVERCLASS $testname #1> $OUTDIR/$i.out 2> $OUTDIR/$i.err
		if [ -s $OUTDIR/$i.err ];then
			echo "$testname" >> $FAILLIST
		else
			echo "$testname" >> $PASSLIST
		fi
	done
}

starttime=`date +%s%N | cut -b1-13`
#RunAllInOne
RunOneByOne
#RunPerClass
#RunAllClasses
stoptime=`date +%s%N | cut -b1-13`
echo "Normal RunTime elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0

# hcai vim :set ts=4 tw=4 tws=4
