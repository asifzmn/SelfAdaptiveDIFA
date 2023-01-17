source ./ad_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/DTInstrumented/

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/DTInstrumented"


suffix="AD"

OUTDIR=$subjectloc/DToutdyn/
mkdir -p $OUTDIR

MAINCLS="adentest.Main1"

starttime=`date +%s%N | cut -b1-13`
set JAVA_OPTS=-DltsRunDiver=true
java -Xmx4g -ea -DltsDebug=true -DuseToken=true -DltsRunDiver=true \
	-cp ${MAINCP} \
	${MAINCLS} \
	10.99.1.191

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0




# source ./Client1.sh

