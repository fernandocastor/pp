#!/bin/bash
JVM=java
JVM_OPTS="-Xmx1024m"
result=""

for i in {0..10000}; do NUMERO[$i]=$RANDOM; done
for i in ${NUMERO[@]}; do result=$result[$i],; done

$JVM $JVM_OPTS QuicksortEsquerda ${NUMERO[@]} &
$JVM $JVM_OPTS QuicksortDireita ${NUMERO[@]} & echo "Threads iniciadas!"







