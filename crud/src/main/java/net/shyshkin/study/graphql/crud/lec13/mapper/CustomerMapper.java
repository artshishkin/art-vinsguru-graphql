package net.shyshkin.study.graphql.crud.lec13.mapper;

import net.shyshkin.study.graphql.crud.lec13.dto.CustomerDto;
import net.shyshkin.study.graphql.crud.lec13.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    Customer toEntity(CustomerDto customerDto);

    CustomerDto toDto(Customer customer);

}
