#!/bin/bash

for i in $*
do
 time java -cp moa.jar moa.DoTask \ "EvaluatePrequential -l $i" &
done
