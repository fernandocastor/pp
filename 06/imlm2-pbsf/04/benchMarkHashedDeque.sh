#!/bin/bash

javac HashedDeque.java
echo "Double locks" > benchmark.txt
echo "2 buckets" >> benchmark.txt
java HashedDeque 2 >> benchmark.txt
echo "4 buckets" >> benchmark.txt
java HashedDeque 4 >> benchmark.txt
echo "8 buckets" >> benchmark.txt
java HashedDeque 8 >> benchmark.txt
echo "16 buckets" >> benchmark.txt
java HashedDeque 16 >> benchmark.txt


javac HashedDequeSingleLock.java

echo "Single lock" >> benchmark.txt

echo "2 buckets" >> benchmark.txt
java HashedDequeSingleLock 2 >> benchmark.txt
echo "4 buckets" >> benchmark.txt
java HashedDequeSingleLock 4 >> benchmark.txt
echo "8 buckets" >> benchmark.txt
java HashedDequeSingleLock 8 >> benchmark.txt
echo "16 buckets" >> benchmark.txt
java HashedDequeSingleLock 16 >> benchmark.txt


