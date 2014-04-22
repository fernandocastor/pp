#!/usr/bin/bash

split --lines=8 $1 pair.

files=$(ls pair*)

for file in $files;
do
    ./build/matricesmultiplier $file &
done
wait
