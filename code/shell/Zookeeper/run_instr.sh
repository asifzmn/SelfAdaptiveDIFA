#!/bin/bash
if [ $# -lt 0 ];then
	echo "Usage: $0"
	exit 1
fi

source ./zk_global.sh

#MAINCP=".:/etc/alternatives/java_sdk/jre/lib/rt.jar:$ROOT/tools/j2sdk1.4.2_18/lib/tools.jar:$ROOT/tools/polyglot-1.3.5/lib/polyglot.jar:$ROOT/tools/soot-2.3.0/lib/sootclasses-2.3.0.jar:$ROOT/tools/jasmin-2.3.0/lib/jasminclasses-2.3.0.jar:$ROOT/tools/java_cup.jar:$subjectloc/build/classes/:$subjectloc/build/test/classes"

INDIR=$subjectloc/distEAInstrumented
MAINCP=".:/opt/jdk1.8.0_101/jre/lib/rt.jar:/opt/jdk1.8.0_101/lib/tools.jar:$ROOT/DistEA/DUA1.jar:$ROOT/DistEA/DistEA.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/conf"

for i in $subjectloc/lib/*.jar;
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
		java -Xmx4000m -ea -DforceMirror=false -DuseToken=true -DltsDebug=true -cp $MAINCP  $DRIVERCLASS $testname #1> $OUTDIR/$i.out 2> $OUTDIR/$i.err
		#java -Xmx4000m -ea -DforceMirror=true -DltsDebug=true -cp $MAINCP  $DRIVERCLASS $testname #1> $OUTDIR/$i.out 2> $OUTDIR/$i.err
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
