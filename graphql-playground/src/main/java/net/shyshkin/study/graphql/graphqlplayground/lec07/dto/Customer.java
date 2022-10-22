package net.shyshkin.study.graphql.graphqlplayground.lec07.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Customer {

    private Integer id;
    private String name;
    private Integer age;
    private String city;

}
