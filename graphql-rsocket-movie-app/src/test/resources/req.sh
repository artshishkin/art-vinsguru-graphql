### request-response using data
rsc --request --route 'graphql' -d '{\"query\": \"query{ping}\"}' --debug tcp://localhost:6565

### request-response using load
rsc --request --route 'graphql' --load ./query-ping.json --debug tcp://localhost:6565

