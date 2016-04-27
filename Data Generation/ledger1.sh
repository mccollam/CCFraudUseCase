#!/bin/bash
#OUTPUT="$(python /Users/eorgad/Demo/CCFraudUseCase/CCFraudUseCase-master/Data_Generation/readrandom0.5.py -i 10  -f 2)"
OUTPUT="$(java -jar /Users/eorgad/Demo/CCFraudUseCase/CCFraudUseCase-master/Data_Generation/parseJsonObjects.jar)"

# echo "What About " $OUTPUT

echo "${OUTPUT}"
