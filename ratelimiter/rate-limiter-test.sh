#!/bin/bash

# Test script to simulate DDoS with a high volume of requests

TARGET_URL="http://localhost/api/cpu"  # Change to any endpoint you want to test
TOTAL_REQUESTS=1000000

for i in $(seq 1 $TOTAL_REQUESTS)
do
  curl -s $TARGET_URL > /dev/null &
done

wait
