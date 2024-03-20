Machine specs are: MacBook Pro 16in, Apple M2 Pro chip, 16 GB RAM, 512 GB HD

1.  100 to 4500 request are pretty stable with mean http response time 2-3 ms 
2.  slowing down from 6000 requests with mean at 10ms
3.  7000 requests mean is 86ms
4.  10000 getting 250ms

Overall express handle well such simple apps, but if we ramp our test to 4000-5000 we can see mongo connection timeout error 