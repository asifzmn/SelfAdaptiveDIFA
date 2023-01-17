starttime=`date +%s%N | cut -b1-13`
./DT2BranchPre.sh
./DT2BranchInstr.sh
stoptime=`date +%s%N | cut -b1-13`
echo "RunTime elapsed: " `expr $stoptime - $starttime` milliseconds	
