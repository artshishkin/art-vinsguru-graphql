package net.shyshkin.study.graphql.client.lec16.clientapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.client.lec16.clientapp.client.CrudClient;
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
    private final CrudClient crudClient;

    @Override
    public void run(String... args) throws Exception {
        Mono.delay(Duration.ofSeconds(1))
                .then(rawQueryDemo())
                .then(getCustomerByIdDemo())
                .then(get2CustomersByIdDemo())
                .then(get2CustomersByIdSolution1Demo())
                .then(getCustomerByIdErrorHandlingDemo())
                .then(getCustomerByIdGenericResponseErrorHandlingDemo())
                .then(getCustomerByIdUnionErrorHandlingDemo())
                //CRUD
                .then(getAllCustomersCrudDemo())
                .then(getCustomerByIdCrudDemo())
                .subscribe();
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

        return executor(mono, "RawQuery", "customers: {}");
    }

    private Mono<Void> getCustomerByIdDemo() {
        return executor(
                customerClient.getCustomerById(1),
                "Query Document",
                "getCustomerById: {}"
        );
    }

    private Mono<Void> get2CustomersByIdDemo() {
        return executor(
                customerClient.get2CustomersByIdMy(1, 2),
                "Two Queries Assignment",
                "get2CustomersByIdMy: {}"
        );
    }

    private Mono<Void> get2CustomersByIdSolution1Demo() {
        return executor(
                customerClient.get2CustomersByIdSolution1(1, 2),
                "Two Queries Assignment - Solution 1",
                "get2CustomersById solution through retrieve: {}"
        );
    }

    private Mono<Void> getCustomerByIdErrorHandlingDemo() {
        Integer absentCustomerId = 100;
        return customerClient
                .getCustomerById(absentCustomerId)

                //.onErrorComplete();
                .onErrorReturn(CustomerDto.builder().build()) //One of possible client error handling

                .doFirst(() -> log.debug("Error handling for Absent customer"))
                .doOnNext(result -> log.debug("Absent customerById {}", result))
                .then();
    }

    private Mono<Void> getCustomerByIdGenericResponseErrorHandlingDemo() {
        return executor(
                customerClient.getCustomerByIdGenericResponse(1),
                "Handling ClientError through GenericResponse - without error",
                "without error: {}")
                .then(executor(
                        customerClient.getCustomerByIdGenericResponse(100),
                        "Handling ClientError through GenericResponse - with error",
                        "with error: {}"
                ));
    }

    private Mono<Void> getCustomerByIdUnionErrorHandlingDemo() {
        return executor(
                customerClient.getCustomerByIdUnion(1),
                "Handling ClientError through Union - without error",
                "without error: {}")
                .then(executor(
                        customerClient.getCustomerByIdUnion(100),
                        "Handling ClientError through Union - with error",
                        "with error: {}"
                ));
    }

    private Mono<Void> getAllCustomersCrudDemo() {
        String preMethodLogMessage = "GET All Customers CRUD";
        String postMethodLogMessage = "customer: {}";
        return crudClient.getAllCustomers()
                .doFirst(() -> log.debug(preMethodLogMessage))
                .doOnNext(result -> log.debug(postMethodLogMessage, result))
                .then();
    }

    private Mono<Void> getCustomerByIdCrudDemo() {
        return executor(
                crudClient.getCustomerById(2),
                "GET Customer by id CRUD",
                "by id: {}");
    }

    private Mono<Void> executor(Mono<?> method, String preMethodLogMessage, String postMethodLogMessage) {
        return method
                .doFirst(() -> log.debug(preMethodLogMessage))
                .doOnNext(result -> log.debug(postMethodLogMessage, result))
                .then();
    }

}
