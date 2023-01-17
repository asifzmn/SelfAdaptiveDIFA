#!/bin/bash
#java -cp /home/xqfu/RandomSentence.jar SimpleRandomSentences
export sentence=$(java -cp /home/xqfu/RandomSentence.jar SimpleRandomSentences)
#java SimpleRandomSentences
#export sentence=$((java SimpleRandomSentences))
echo $sentence
