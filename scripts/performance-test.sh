#!/usr/bin/env bash
set -euo pipefail

URL="${1:-http://localhost:8080/}"
REQUESTS=500
CONCURRENCY=10

echo "Starting automated performance test..."
echo "Target: $URL"
echo "Requests: $REQUESTS"
echo "Concurrent users: $CONCURRENCY"

ab -n "$REQUESTS" -c "$CONCURRENCY" "$URL" | tee performance-results.txt

awk '
/Failed requests:/ { failed=$3; found=1 }
/^Non-2xx responses:/ { non2xx=$3 }
/^Time per request:/ && !seen { latency=$4; seen=1 }
END {
    if (!found || !seen || failed > 0 || non2xx > 0 || latency > 500) {
        print "PERFORMANCE TEST FAILED"
        exit 1
    }
    print "PERFORMANCE TEST PASSED"
}' performance-results.txt
