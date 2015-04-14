#! /bin/bash

files=("big.txt" "big2.txt" "big3.txt" "big4.txt")

for i in "${files[@]}"
do
	./count $i > $i.out &
done

wait

find -name "*.out" | xargs ./join