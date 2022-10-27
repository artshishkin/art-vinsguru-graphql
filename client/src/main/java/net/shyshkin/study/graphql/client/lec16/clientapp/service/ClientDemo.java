package net.shyshkin.study.graphql.client.lec16.clientapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.client.lec16.clientapp.client.CustomerClient;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientDemo implements CommandLineRunner {

    private final CustomerClient customerClient;

    @Override
    public void run(String... args) throws Exception {
        rawQueryDemo().subscribe();
    }

    private Mono<Void> rawQueryDemo() {

        log.debug("Starting rawQueryDemo...");

        String query = "{\n" +
                "   c_alias: customers {\n" +
                "       id \n" +
                "       name\n" +
                "       age\n" +
                "       city\n" +
                "   }\n" +
                "}";

        Mono<List<CustomerDto>> mono = customerClient.rawQuery(query)
                .map(cr -> cr.field("c_alias").toEntityList(CustomerDto.class));

        return Mono.delay(Duration.ofSeconds(1))
                .doFirst(() -> log.debug("RawQuery"))
                .then(mono)
                .doOnNext(list -> log.debug("customers: {}", list))
                .then();
    }
}
