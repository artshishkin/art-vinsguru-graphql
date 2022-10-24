package net.shyshkin.study.graphql.crud.lec13.repository;

import net.shyshkin.study.graphql.crud.lec13.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRepository extends R2dbcRepository<Customer, Integer> {
}
