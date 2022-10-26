package net.shyshkin.study.graphql.errorhandling.lec15.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.CustomerDto;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.DeleteResultDto;
import net.shyshkin.study.graphql.errorhandling.lec15.dto.Status;
import net.shyshkin.study.graphql.errorhandling.lec15.exception.ApplicationErrors;
import net.shyshkin.study.graphql.errorhandling.lec15.exception.ApplicationException;
import net.shyshkin.study.graphql.errorhandling.lec15.mapper.CustomerMapper;
import net.shyshkin.study.graphql.errorhandling.lec15.repository.CustomerRepository;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

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
                .switchIfEmpty(ApplicationErrors.noSuchCustomer(id))
                .map(mapper::toDto);
    }

    public Mono<CustomerDto> createCustomer(CustomerDto dto) {
        if (dto.getAge() < 18)
            return Mono.error(new ApplicationException(
                    ErrorType.BAD_REQUEST,
                    "Age must be greater then 18",
                    Map.of("customer", dto))
            );
        return Mono.just(dto)
                .map(mapper::toEntity)
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<CustomerDto> updateCustomer(Integer id, CustomerDto dto) {
        return repository.findById(id)
                .switchIfEmpty(ApplicationErrors.noSuchCustomer(id))
                .map(a -> dto)
                .map(mapper::toEntity)
                .doOnNext(c -> c.setId(id))
                .flatMap(repository::save)
                .map(mapper::toDto);
    }

    public Mono<DeleteResultDto> deleteCustomer(Integer id) {
        return repository.findById(id)
                .switchIfEmpty(ApplicationErrors.noSuchCustomer(id))
                .then(repository.deleteById(id))
                .thenReturn(DeleteResultDto.builder().id(id).status(Status.SUCCESS).build())
                .onErrorReturn(DeleteResultDto.builder().id(id).status(Status.FAILURE).build());
    }

}
