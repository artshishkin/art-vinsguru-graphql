package net.shyshkin.study.graphql.client.lec16.serverapp.repository;

import net.shyshkin.study.graphql.client.lec16.serverapp.entity.Customer;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

public interface CustomerRepository extends R2dbcRepository<Customer, Integer> {
}
