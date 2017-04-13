#!/bin/bash

for filename in `ls $1/*.txt`; do
	./plotter.py -i $filename
done
