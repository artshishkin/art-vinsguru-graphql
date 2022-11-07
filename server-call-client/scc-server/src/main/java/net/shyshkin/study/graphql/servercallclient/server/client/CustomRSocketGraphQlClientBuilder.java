package net.shyshkin.study.graphql.servercallclient.server.client;

import org.springframework.graphql.client.AbstractDelegatingGraphQlClient;
import org.springframework.graphql.client.AbstractGraphQlClientBuilder;
import org.springframework.graphql.client.GraphQlClient;
import org.springframework.graphql.client.RSocketGraphQlClient;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

public class CustomRSocketGraphQlClientBuilder
        extends AbstractGraphQlClientBuilder<CustomRSocketGraphQlClientBuilder> {

    private final RSocketRequester requester;

    private String route;

    public CustomRSocketGraphQlClientBuilder(RSocketRequester requester) {
        this.requester = requester;
        this.route = "graphql";
        setJsonCodecs(new Jackson2JsonEncoder(), new Jackson2JsonDecoder());
    }

    public CustomRSocketGraphQlClientBuilder route(String route) {
        Assert.notNull(route, "'route' is required");
        this.route = route;
        return this;
    }

    @Override
    public RSocketGraphQlClient build() {

        Assert.notNull(requester, "RSocketRequester is required");

        RSocketGraphQlTransport graphQlTransport = new RSocketGraphQlTransport(this.route, requester, getJsonDecoder());

        return new CustomRSocketGraphQlClient(
                super.buildGraphQlClient(graphQlTransport), requester);
    }

    private static class CustomRSocketGraphQlClient extends AbstractDelegatingGraphQlClient implements RSocketGraphQlClient {

        private final RSocketRequester requester;

//		private final RSocketRequester.Builder requesterBuilder;

//		private final ClientTransport clientTransport;

//		private final String route;

//		private final Consumer<AbstractGraphQlClientBuilder<?>> builderInitializer;

        //		CustomRSocketGraphQlClient(
//				GraphQlClient graphQlClient, RSocketRequester requester, RSocketRequester.Builder requesterBuilder,
//				ClientTransport clientTransport, String route, Consumer<AbstractGraphQlClientBuilder<?>> builderInitializer) {

        CustomRSocketGraphQlClient(GraphQlClient graphQlClient, RSocketRequester requester) {


            super(graphQlClient);

            this.requester = requester;
//			this.requesterBuilder = requesterBuilder;
//			this.clientTransport = clientTransport;
//			this.route = route;
//			this.builderInitializer = builderInitializer;
        }

        @Override
        public Mono<Void> start() {
            return this.requester.rsocketClient().source().then();
        }

        @Override
        public Mono<Void> stop() {
            // Currently, no option to close and wait (see Javadoc)
            this.requester.dispose();
            return Mono.empty();
        }

        @Override
        public RSocketGraphQlClient.Builder<?> mutate() {
            throw new RuntimeException("Not supported");
//			CustomRSocketGraphQlClientBuilder builder = new CustomRSocketGraphQlClientBuilder(this.requesterBuilder);
//			builder.clientTransport(this.clientTransport);
//			builder.route(this.route);
//			this.builderInitializer.accept(builder);
//			return builder;
        }

    }

}
