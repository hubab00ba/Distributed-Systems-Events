#!/bin/bash

# This script requires Apache JMeter app to be installed and available in the PATH, see: https://jmeter.apache.org/download_jmeter.cgi

CURR_DIR=$(pwd)
TELEMETRY_FILEPATH="$CURR_DIR/apache_jmeter/telemetry"
CONFIG_FILEPATH="$CURR_DIR/config/StressTest.jmx"

#TODO: add Taurus wrapper
# Execute 50 iteration of 2200 concurrent requests (one user invokes one request on each of two endpoints) which in total equals 99000 requests.
# The number of users for each iteration ramps up from 0 to 1100 in 10 seconds
jmeter -n -t $CONFIG_FILEPATH -JtelemetryPath $TELEMETRY_FILEPATH -JusersNum 1100 -JrampupSeconds 10 -JiterationNum 50
#echo jmeter -n -t $CONFIG_FILEPATH -JtelemetryPath $TELEMETRY_FILEPATH -JusersNum 1500 -JrampupSeconds 10 -JiterationNum 50