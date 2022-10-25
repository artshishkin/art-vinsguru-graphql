package net.shyshkin.study.graphql.crud.lec14.mapper;

import net.shyshkin.study.graphql.crud.lec14.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec14.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDto customerDto);

    CustomerDto toDto(Customer customer);

}
