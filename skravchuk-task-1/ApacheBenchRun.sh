#!/bin/bash

# This script requires Apache Bench app to be installed and available in the PATH, see: https://www.apachelounge.com/download/

# Execute 100000 requests (each 2200 requests are concurrent) using Keep Alive and Dynamic document length features
ab -n 100000 -c 2200 -k -l -T application/json -p ./samples/userSample.json -g ./apache_bench/telemetry_app_with_db http://localhost:8080/user/create >> ./apache_bench/report_app_with_db

# Execute 100000 requests (each 4000 requests are concurrent) using Keep Alive and Dynamic document length features
ab -n 100000 -c 4000 -k -l -g ./apache_bench/telemetry_app_without_db http://localhost:8080/user/sample >> ./apache_bench/report_app_without_db