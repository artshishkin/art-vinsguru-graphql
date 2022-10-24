package net.shyshkin.study.graphql.crud.lec13.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.crud.lec13.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec13.dto.DeleteResultDto;
import net.shyshkin.study.graphql.crud.lec13.dto.Status;
import net.shyshkin.study.graphql.crud.lec13.mapper.CustomerMapper;
import net.shyshkin.study.graphql.crud.lec13.repository.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Profile("lec13")
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final CustomerMapper mapper;

    public Flux<CustomerDto> getAllCustomers() {
        return repository.findAll()
                .map(mapper::toDto);
    }

    public Mono<CustomerDto> getCustomerById(Integer id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    public Mono<CustomerDto> createCustomer(CustomerDto dto) {
        return Mono.just(dto)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, CustomerDto dto) {
        return repository.findById(id)
                .map(a -> dto)
                .map(mapper::toEntity)
                .doOnNext(c -> c.setId(id))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<DeleteResultDto> deleteCustomer(Integer id) {
        return repository.deleteById(id)
                .map(v -> DeleteResultDto.builder().id(id).status(Status.SUCCESS).build())
                .defaultIfEmpty(DeleteResultDto.builder().id(id).status(Status.FAILURE).build())
                .onErrorReturn(DeleteResultDto.builder().id(id).status(Status.FAILURE).build());
    }

}
