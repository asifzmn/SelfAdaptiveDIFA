starttime1=`date +%s%N | cut -b1-13`
./DT2BranchPre.sh
./DT2BranchInstr.sh
stoptime1=`date +%s%N | cut -b1-13`
echo "RunTime elapsed: " `expr $stoptime1 - $starttime1` milliseconds	
