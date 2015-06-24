#! /bin/bash

for txt in `ls sample/*.txt`
do
	./topn_lite $txt &
done

wait
