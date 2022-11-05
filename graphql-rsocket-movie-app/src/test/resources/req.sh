### request-response using data - ping
rsc --request --route 'graphql' -d '{\"query\": \"query{ping}\"}' --debug tcp://localhost:6565

### request-response using load - ping
rsc --request --route 'graphql' --load ./query-ping.json --debug tcp://localhost:6565

### get user profile
rsc --request --route 'graphql' --load ./query-user-profile.json --debug tcp://localhost:6565

### get movie details
rsc --request --route 'graphql' --load ./query-movie-details.json --debug tcp://localhost:6565

### get movies by genre
rsc --request --route 'graphql' --load ./query-movies-by-genre.json --debug tcp://localhost:6565

### mutation update user profile
rsc --request --route 'graphql' --load ./mutation-update-user-profile.json --debug tcp://localhost:6565

### mutation add to watchlist
rsc --request --route 'graphql' --load ./mutation-add-to-watchlist.json --debug tcp://localhost:6565

### query schema with minimal info
rsc --request --route 'graphql' --load ./query-schema-minimal.json --debug tcp://localhost:6565

### query schema introspection
rsc --request --route 'graphql' --load ./query-schema-introspection.json tcp://localhost:6565 > schema-introspection.json


