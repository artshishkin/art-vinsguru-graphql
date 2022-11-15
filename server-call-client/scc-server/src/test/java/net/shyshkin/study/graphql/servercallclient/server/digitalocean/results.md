### Results comparison

#### Local Test

```
15:41:36.712 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - Started ComplexQueriesServersideLBIT in 4.532 seconds (JVM running for 17.029)
15:41:37.092 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
15:41:37.277 [reactor-http-nio-3] INFO  reactor.Mono.SinkEmptyMulticast.1 - onSubscribe(SinkEmptyMulticast.VoidInner)
15:41:37.279 [reactor-http-nio-3] INFO  reactor.Mono.SinkEmptyMulticast.1 - request(unbounded)
-------------------------------
15:41:39.154 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:2, moviesCount:5) `rest`: PT1.7531815S
-------------------------------
15:41:39.169 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:39.631 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:2, moviesCount:5) `graphql`: PT0.3513465S
-------------------------------
15:41:39.638 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:40.723 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:10) `rest`: PT0.9725175S
-------------------------------
15:41:40.729 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:41.626 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:10) `graphql`: PT0.7950474S
-------------------------------
15:41:41.636 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:43.524 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:30) `rest`: PT1.7797826S
-------------------------------
15:41:43.533 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:45.038 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:30) `graphql`: PT1.3942763S
-------------------------------
15:41:45.043 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:46.912 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:50) `rest`: PT1.7484025S
-------------------------------
15:41:46.918 [main] INFO  n.s.s.g.s.s.c.BaseServersideLoadBalancedIT - Waiting for client: ec09322a-d94b-46d8-8992-d3de9674c457 (ComplexQueriesServersideLBIT)
-------------------------------
15:41:48.686 [main] INFO  n.s.s.g.s.s.c.ComplexQueriesServersideLBIT - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:50) `graphql`: PT1.6587486S
-------------------------------
```

#### Digital Ocean

```
115:37:25.882 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - Started ComplexQueriesDigitalOceanManualTest in 4.498 seconds (JVM running for 14.859)
-------------------------------
15:37:28.320 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:2, moviesCount:5) `rest`: PT2.0967011S
-------------------------------
-------------------------------
15:37:28.917 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:2, moviesCount:5) `graphql`: PT0.5823616S
-------------------------------
-------------------------------
15:37:29.969 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:10) `rest`: PT1.041586S
-------------------------------
-------------------------------
15:37:31.021 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:10) `graphql`: PT1.0458039S
-------------------------------
-------------------------------
15:37:32.797 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:30) `rest`: PT1.769171S
-------------------------------
-------------------------------
15:37:34.530 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:30) `graphql`: PT1.7263567S
-------------------------------
-------------------------------
15:37:36.308 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:50) `rest`: PT1.7692002S
-------------------------------
-------------------------------
15:37:38.347 [main] INFO  n.s.s.g.s.s.d.ComplexQueriesDigitalOceanManualTest - 
Duration of complexWatchListUpdate (usersCount:4, moviesCount:50) `graphql`: PT2.0325654S
-------------------------------
```
