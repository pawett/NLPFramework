#!/bin/bash

IFS='
'
linen=0;
for line in `cat $1`;do
	linen=$(($linen + 1))
	cols=`echo $line | wc -w`
	echo "$linen"
	if [ $cols -ne 6 ]; then
	echo "cols $cols line $linen ---- $line"
	exit 0;
	fi
done



