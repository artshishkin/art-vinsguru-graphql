package net.shyshkin.study.graphql.graphqlplayground.lec07.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CustomerWithOrdersDto extends Customer {

    private List<CustomerOrderDto> orders;

}
