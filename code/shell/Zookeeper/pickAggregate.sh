#!/bin/bash

cat $1 | \
	awk -F'\t' '{if($1=="[AggregatePerQuery]") { for (i=2;i<=NF;i++) printf("%s\t", $i); printf("\n");}}'
