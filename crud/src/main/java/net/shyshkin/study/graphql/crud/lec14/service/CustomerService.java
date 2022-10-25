package net.shyshkin.study.graphql.crud.lec14.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.crud.lec14.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec14.dto.DeleteResultDto;
import net.shyshkin.study.graphql.crud.lec14.dto.Status;
import net.shyshkin.study.graphql.crud.lec14.mapper.CustomerMapper;
import net.shyshkin.study.graphql.crud.lec14.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
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
                .thenReturn(DeleteResultDto.builder().id(id).status(Status.SUCCESS).build())
                .onErrorReturn(DeleteResultDto.builder().id(id).status(Status.FAILURE).build());
    }

}
