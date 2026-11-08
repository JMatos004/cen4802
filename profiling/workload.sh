#!/bin/bash
duration=${1:-60}
end=$((SECONDS + duration))
count=0

echo "Submitting scores for $duration seconds..."
while [ "$SECONDS" -lt "$end" ]; do
  curl --fail --silent --show-error --max-time 5 \
    -o /dev/null \
    -d "player1=John&games1=5&player2=Luke&games2=3" \
    http://localhost:8080/ || exit 1
  count=$((count + 1))
done

echo "Completed $count successful score submissions."
