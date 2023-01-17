source ./hd_global.sh

#INDIR=$subjectloc/distEAInstrumented
#INDIR=$subjectloc/build.sv/classes/
#INDIR=$subjectloc/build/classes/
INDIR=$subjectloc/DT2BrPre/

MAINCP=".:/usr/lib/jvm/java-8-openjdk-amd64/jre/lib/rt.jar:$ROOT/DUA1.jar:$ROOT/FLOWDIST.jar:$ROOT/libs/soot-trunk.jar:$subjectloc/DT2BrPre"

suffix="hd"

OUTDIR=$subjectloc/DToutdyn/
mkdir -p $OUTDIR

MAINCLS="org.hsqldb.Server"

starttime=`date +%s%N | cut -b1-13`
set JAVA_OPTS=-DltsRunDiver=true
java -Xmx40g -ea -DltsDebug=true -DuseToken=true -DltsRunDiver=true \
	-cp ${MAINCP} \
	${MAINCLS} \
	-database.0 db/mydb -dbname.0 xdb

stoptime=`date +%s%N | cut -b1-13`

echo "RunTime for $suffix elapsed: " `expr $stoptime - $starttime` milliseconds
exit 0


# java -cp ".:/home/xqfu/hsqldb/251/hsqldb/lib/hsqldb.jar" org.hsqldb.Server -database.0 /home/xqfu/hsqldb/251/hsqldb/data/mydb -dbname.0 xdb


