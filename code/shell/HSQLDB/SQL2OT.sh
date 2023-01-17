source ./hd_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/OTInstrumented/

# MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/DT2Instrumented:/home/xqfu/hsqldb/251/hsqldb/lib/hsqldb.jar"

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/OTInstrumented"
suffix="hd"

starttime=`date +%s%N | cut -b1-13`

OUTDIR=$subjectloc/OToutdyn/
mkdir -p $OUTDIR

MAINCLS="org.hsqldb.cmdline.SqlTool"

starttime=`date +%s%N | cut -b1-13`
set JAVA_OPTS=-DltsRunDiver=true
java -Xmx100g -ea -DltsDebug=false -DuseToken=true -DltsRunDiver=true \
	-cp ${MAINCP} \
	${MAINCLS} \
    --sql="SELECT * FROM INFORMATION_SCHEMA.TABLES;" \
	--rcfile sqltool.rc xdb 

	stoptime=`date +%s%N | cut -b1-13`
echo "Time: " `expr $stoptime - $starttime` milliseconds

echo "Running finished."
exit 0
