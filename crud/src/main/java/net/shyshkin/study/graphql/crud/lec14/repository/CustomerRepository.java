package net.shyshkin.study.graphql.crud.lec14.repository;

import net.shyshkin.study.graphql.crud.lec14.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRepository extends R2dbcRepository<Customer, Integer> {
}
