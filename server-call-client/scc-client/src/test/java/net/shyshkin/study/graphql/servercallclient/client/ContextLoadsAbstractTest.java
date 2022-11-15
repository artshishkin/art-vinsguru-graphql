package net.shyshkin.study.graphql.servercallclient.client;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.rsocket.server.port: 0"
})
@DirtiesContext
public abstract class ContextLoadsAbstractTest {

    @Test
    void contextLoads() {
    }

}