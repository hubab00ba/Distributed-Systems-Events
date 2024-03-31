This is a simple Spring Boot application that utilizes MongoDB as a main data storage and serves as an example of using different tools to benchmark the application.

It has two endpoints:
1. `/user/create` - a POST endpoint that saves a user into DB and returns the user data back to a client
2. `/user/sample` - a simple GET endpoint that returns a static piece of data

Currently, two load testing tools has been used:
- Apache Bench
- Apache JMeter

PC Specs:
- CPU: 11th Gen Intel(R) Core(TM) i7-11370H @ 3.30GHz (4 physical cores / 8 logical cores)
- RAM: DDR4 32GB 3200MHz
- OS: Windows 10


Load test procedure:

Consider the performance requirement for this app is that the user should see a response from the application within 2 seconds.

Let's run different load testing tools to determine performance characteristics of the application. 

## Apache Bench
One of the largest drawbacks of using is that only one endpoint can be load tested at a time.
So, both endpoint were loaded separately.

After series of 30-60 seconds long runs of load tests, the following conclusions are made:
1. `/user/create` - the requirement would be satisfied only when the concurrent users count is less than 4000.
2. `/user/sample` - the requirement can be satisfied even when the concurrent users count is more than 4000.

Considering hte real world usage scenario where all endpoints are somewhat loaded, the application should handle >=3000 concurrent users

Apache Bench `/user/create` endpoint full report:
```
Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /user/create
Document Length:        Variable

Concurrency Level:      4000
Time taken for tests:   45.089 seconds
Complete requests:      100000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      56669706 bytes
Total body sent:        36100000
HTML transferred:       42969706 bytes
Requests per second:    2217.85 [#/sec] (mean)
Time per request:       1803.551 [ms] (mean)
Time per request:       0.451 [ms] (mean, across all concurrent requests)
Transfer rate:          1227.39 [Kbytes/sec] received
                        781.88 kb/s sent
                        2009.27 kb/s total

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.3      0       3
Processing:   331 1777 201.6   1782    2113
Waiting:        2  904 495.4    898    2064
Total:        331 1777 201.6   1782    2113

Percentage of the requests served within a certain time (ms)
  50%   1782
  66%   1818
  75%   1852
  80%   1873
  90%   1956
  95%   2070
  98%   2101
  99%   2104
 100%   2113 (longest request)

```

Apache Bench `/user/sample` endpoint full report:
```
Server Software:        
Server Hostname:        localhost
Server Port:            8080

Document Path:          /user/sample
Document Length:        Variable

Concurrency Level:      4000
Time taken for tests:   29.320 seconds
Complete requests:      100000
Failed requests:        0
Keep-Alive requests:    0
Total transferred:      54869268 bytes
HTML transferred:       41169268 bytes
Requests per second:    3410.69 [#/sec] (mean)
Time per request:       1172.783 [ms] (mean)
Time per request:       0.293 [ms] (mean, across all concurrent requests)
Transfer rate:          1827.56 [Kbytes/sec] received

Connection Times (ms)
              min  mean[+/-sd] median   max
Connect:        0    0   0.3      0       3
Processing:   412 1154 283.6   1233    1763
Waiting:        1  587 345.2    553    1729
Total:        412 1154 283.7   1233    1763

Percentage of the requests served within a certain time (ms)
  50%   1233
  66%   1299
  75%   1361
  80%   1401
  90%   1468
  95%   1582
  98%   1710
  99%   1727
 100%   1763 (longest request)

```


## Apache JMeter
This tool has greater capabilities than Apache Bench so the load testing is done in a bit different way. 
Nevertheless, here is the similar test where only one endpoint (`/user/create`) is loaded with a fixed number of concurrent users.

Concurrent users: 4000
Total requests: 100000

```
summary +  42496 in 00:00:11 = 3933.0/s Avg:   370 Min:     0 Max:  1491 Err:     0 (0.00%) Active: 3771 Started: 4000 Finished: 229
summary +  57504 in 00:00:14 = 4071.7/s Avg:   794 Min:     1 Max:  1180 Err:     0 (0.00%) Active: 0 Started: 4000 Finished: 4000
summary = 100000 in 00:00:25 = 4010.6/s Avg:   614 Min:     0 Max:  1491 Err:     0 (0.00%)
```
As you can see the results are better, this is simply because the load testing process is divided into iterations and all users were reused for each new iteration.
It is possible to create a new user for each new request but this requires some network settings tuning on the machine (at least on Windows machine).
Unfortunately, the tuning is not covered in during these tests.


Here are results for load tests of both endpoints simultaneously:
Concurrent users: 4000
Total requests: 200000 (100000 per each endpoint) 

```
summary +  12308 in 00:00:03 = 3554.1/s Avg:    79 Min:     0 Max:  1431 Err:     0 (0.00%) Active: 1755 Started: 1760 Finished: 5
summary + 162205 in 00:00:30 = 5407.0/s Avg:   525 Min:     0 Max:  2996 Err:     0 (0.00%) Active: 2866 Started: 4000 Finished: 1134
summary = 174513 in 00:00:33 = 5215.3/s Avg:   494 Min:     0 Max:  2996 Err:     0 (0.00%)
summary +  25487 in 00:00:06 = 4257.8/s Avg:   460 Min:     0 Max:  1265 Err:     0 (0.00%) Active: 0 Started: 4000 Finished: 4000
summary = 200000 in 00:00:39 = 5069.7/s Avg:   489 Min:     0 Max:  2996 Err:     0 (0.00%)
```

Even with user reuse, the requirement is failed.

Decreasing the number of concurrent users helps to meet the requirement:

Concurrent users: 2500
Total requests: 100000

```
summary +  36766 in 00:00:09 = 4159.5/s Avg:   168 Min:     0 Max:  1985 Err:     0 (0.00%) Active: 2414 Started: 2500 Finished: 86
summary +  63234 in 00:00:15 = 4225.5/s Avg:   459 Min:     0 Max:  1302 Err:     0 (0.00%) Active: 0 Started: 2500 Finished: 2500
summary = 100000 in 00:00:24 = 4200.4/s Avg:   352 Min:     0 Max:  1985 Err:     0 (0.00%)
```