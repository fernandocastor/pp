#! /bin/bash

for txt in `ls *.txt`
do
	./topn_lite $txt > /dev/null &
done

wait
