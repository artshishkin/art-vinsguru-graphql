package net.shyshkin.study.graphql.graphqlplayground.lec05.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Account;
import net.shyshkin.study.graphql.graphqlplayground.lec05.dto.Customer;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static net.shyshkin.study.graphql.graphqlplayground.lec05.dto.AccountType.CHECKING;
import static net.shyshkin.study.graphql.graphqlplayground.lec05.dto.AccountType.SAVING;

@Slf4j
@Controller
public class AccountController {

//    @SchemaMapping(typeName = "Customer") //we can skip typeName if we have it in method arguments
    @SchemaMapping
    public Mono<Account> account(Customer customer) {
        log.debug("fetch account of {}", customer);
        return Mono.fromSupplier(() -> Account.builder()
                .id(UUID.randomUUID())
                .accountType(ThreadLocalRandom.current().nextBoolean() ? CHECKING : SAVING)
                .amount(ThreadLocalRandom.current().nextInt(1_000))
                .build());
    }

}
