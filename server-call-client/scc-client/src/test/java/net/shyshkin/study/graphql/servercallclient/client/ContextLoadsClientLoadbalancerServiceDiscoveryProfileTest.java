package net.shyshkin.study.graphql.servercallclient.client;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("client-loadbalance-service-discovery")
class ContextLoadsClientLoadbalancerServiceDiscoveryProfileTest extends ContextLoadsAbstractTest {
}