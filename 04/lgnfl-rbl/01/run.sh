SAMPLES=10
N=8
K=4294967296

i=1
until [ $i = `expr $SAMPLES + 1` ] ; do
    time java StatisticalCounting $N $K

    i=`expr $i + 1`
done
