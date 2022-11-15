package net.shyshkin.study.graphql.servercallclient.client;

import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("server-loadbalance")
class ContextLoadsServerLoadbalancerProfileTest extends ContextLoadsAbstractTest {
}