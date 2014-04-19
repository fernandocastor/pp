#!/bin/bash

jobs=8

# Function to be backgrounded
batchJob() {
  #printf "job %d got range %d-%d\n" $1 $2 $3
  ./run_single $2 $3 > ./out.$1
}

aSize=$(head -n 1 A.txt)
aRows=$(echo $aSize | cut -f1 -d" ")
aCols=$(echo $aSize | cut -f2 -d" ")
bSize=$(head -n 1 B.txt)
bRows=$(echo $bSize | cut -f1 -d" ")
bCols=$(echo $bSize | cut -f2 -d" ")
if [ $aCols -ne $bRows ]
then
  # matrixes cannot be multiplied, exit early
  exit 1
fi
cRows=$aRows
sizePerJob=$(expr $cRows / $jobs)
sizePerJob=$(($sizePerJob<1?1:$sizePerJob))

declare -A pids

# background each job
jobStart="0"
for n in $(seq 1 $jobs); do
  if [ $jobStart -eq $cRows ]; then break; fi
  jobEnd=$(expr $jobStart + $sizePerJob)
  if [ $n -eq $jobs ]; then jobEnd=$cRows; fi
  jobEnd=$(($jobEnd>$cRows?$cRows:$jobEnd))
  batchJob $n $jobStart $jobEnd &
  pid=$!
  #echo "backgrounded: $n (pid=$pid)"
  pids[$pid]=$n
  jobStart=$jobEnd
done

# watch state of backgrounded processes and remove them as they go away
# as it was suggested in http://stackoverflow.com/a/11088520
while [ -n "${pids[*]}" ]; do
  sleep 0
  for pid in "${!pids[@]}"; do
    if ! ps "$pid" >/dev/null; then
      unset pids[$pid]
    fi
  done
  if [ -z "${!pids[*]}" ]; then
    break
  fi
done

# merge output
for n in $(seq 1 $jobs); do
  filename="out.$n"
  if [ -e $filename ]; then
    cat $filename
    rm $filename
  else
  	break
  fi
done
