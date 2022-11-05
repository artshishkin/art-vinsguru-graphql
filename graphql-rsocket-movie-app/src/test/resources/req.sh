### request-response using data - ping
rsc --request --route 'graphql' -d '{\"query\": \"query{ping}\"}' --debug tcp://localhost:6565

### request-response using load - ping
rsc --request --route 'graphql' --load ./query-ping.json --debug tcp://localhost:6565

### get user profile
rsc --request --route 'graphql' --load ./query-user-profile.json --debug tcp://localhost:6565### get user profile

### get movie details
rsc --request --route 'graphql' --load ./query-movie-details.json --debug tcp://localhost:6565