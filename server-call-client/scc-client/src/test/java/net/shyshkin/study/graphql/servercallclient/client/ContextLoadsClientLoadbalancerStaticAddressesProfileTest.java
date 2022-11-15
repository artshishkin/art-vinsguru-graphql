package net.shyshkin.study.graphql.servercallclient.client;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("client-loadbalance-static-addresses")
class ContextLoadsClientLoadbalancerStaticAddressesProfileTest extends ContextLoadsAbstractTest {
}