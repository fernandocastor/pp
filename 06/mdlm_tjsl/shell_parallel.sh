#!/bin/bash

# n m
# a11 a12 ... a1m
# a21 a22 ... a2m
# ...
# an1 an2 ... anm
#
# m p
# b11 b12 ... b1p
# b21 b22 ... b2p
# ...
# bm1 bm2 ... bmp

if [ "$#" -lt 2 ]
then
	echo "Too few arguments"
	exit 1
fi

arq_a="$1"
arq_b="$2"

first_line_a=$(cat "$arq_a" | head -n 1)
n_a=$(echo "$first_line_a" | cut -d ' ' -f 1)
m_a=$(echo "$first_line_a" | cut -d ' ' -f 2)

first_line_b=$(cat "$arq_b" | head -n 1)
m_b=$(echo "$first_line_b" | cut -d ' ' -f 1)
p_b=$(echo "$first_line_b" | cut -d ' ' -f 2)

if [ "$m_a" -ne "$m_b" ]
then
	echo "Invalid matrices"
	exit 1
fi

matrix_a=$(tail -n +2 "$arq_a")

for x in $(seq 2 $((n_a+1)))
do
	sed -n "${x}p" "$arq_a" | ./row_matrix_mult "$arq_b" > "$x".row &
done

for x in $(seq 2 $((n_a+1)))
do
	cat "$x".row
	rm "$x".row
done
