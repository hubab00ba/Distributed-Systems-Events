This module provides an example of data inconsistency in microservice world.

Two services were used for the example:
1. Write-oriented service - provides an API to store data in database and sends a view of the data to second service for replication.
2. Read-oriented service - provides an API to read stored data from a replication database.


The data flow is the following:
1. Client sends a request to Write Service to create a user entry.
2. Write Service saves the given user data into a database, notifies the Read Service that a new user was created and returns the id of the new entry back to the Client.
3. Read Service receives the notification and saves user data view in its database.
4. Client sends a request to Read Service to get user data by a given ID.
5. Read Service returns user data by a given ID.

The inconsistency can occur when a new user record is created on the Write Service side, but not yet copied on the Read Service side, and the Client tries to retrieve the user entry data.
The system was stress tested using JMeter+Taurus and Artillery tools to prove this statement.

Artillery showed that inconsistency starting to show itself at a request rate of ~250-300/sec.
JMeter+Taurus proved the same performance.

Note: looks like out-of-the-box settings of JMeter/Taurus are configured for speedy requests, so to get closer to the Artillery results a small delay was added for each request. As a result, the test duration is the same as run without request delay but the application can handle around the same amount of requests as was reported by Artillery.
The configuration of JMeter/Taurus should be investigated further.

Artillery full report:
```
Test run id: tmfxr_83erzk8kgqqgt59ekq69kexh76h43_3je6
Phase started: unnamed (index: 0, duration: 1s) 21:24:06(+0300)

Phase completed: unnamed (index: 0, duration: 1s) 21:24:07(+0300)

--------------------------------------
Metrics for period to: 21:24:10(+0300) (width: 0.555s)
--------------------------------------

http.codes.200: ................................................................ 310
http.downloaded_bytes: ......................................................... 68330
http.request_rate: ............................................................. 310/sec
http.requests: ................................................................. 310
http.response_time:
  min: ......................................................................... 2
  max: ......................................................................... 82
  mean: ........................................................................ 27.4
  median: ...................................................................... 19.9
  p95: ......................................................................... 61
  p99: ......................................................................... 74.4
http.responses: ................................................................ 310
vusers.completed: .............................................................. 155
vusers.created: ................................................................ 155
vusers.created_by_name.Compare the responses from the 2 services: .............. 155
vusers.failed: ................................................................. 0
vusers.session_length:
  min: ......................................................................... 52.5
  max: ......................................................................... 202.9
  mean: ........................................................................ 124.2
  median: ...................................................................... 127.8
  p95: ......................................................................... 169
  p99: ......................................................................... 186.8


All VUs finished. Total time: 2 seconds

--------------------------------
Summary report @ 21:24:08(+0300)
--------------------------------

http.codes.200: ................................................................ 310
http.downloaded_bytes: ......................................................... 68330
http.request_rate: ............................................................. 310/sec
http.requests: ................................................................. 310
http.response_time:
  min: ......................................................................... 2
  max: ......................................................................... 82
  mean: ........................................................................ 27.4
  median: ...................................................................... 19.9
  p95: ......................................................................... 61
  p99: ......................................................................... 74.4
http.responses: ................................................................ 310
vusers.completed: .............................................................. 155
vusers.created: ................................................................ 155
vusers.created_by_name.Compare the responses from the 2 services: .............. 155
vusers.failed: ................................................................. 0
vusers.session_length:
  min: ......................................................................... 52.5
  max: ......................................................................... 202.9
  mean: ........................................................................ 124.2
  median: ...................................................................... 127.8
  p95: ......................................................................... 169
  p99: ......................................................................... 186.8

```

JMeter+Taurus report:
```
21:39:18 INFO: Test duration: 0:00:04
21:39:18 INFO: Samples count: 300, 0.00% failures
21:39:18 INFO: Average times: total 0.010, latency 0.010, connect 0.003
21:39:18 INFO: Percentiles:
+---------------+---------------+
| Percentile, % | Resp. Time, s |
+---------------+---------------+
|           0.0 |         0.002 |
|          50.0 |         0.004 |
|          90.0 |         0.026 |
|          95.0 |         0.058 |
|          99.0 |         0.065 |
|          99.9 |         0.065 |
|         100.0 |         0.065 |
+---------------+---------------+
```
