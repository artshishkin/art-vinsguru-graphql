package net.shyshkin.study.graphql.client.lec16.serverapp.mapper;

import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import net.shyshkin.study.graphql.client.lec16.serverapp.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDto customerDto);

    CustomerDto toDto(Customer customer);

}
