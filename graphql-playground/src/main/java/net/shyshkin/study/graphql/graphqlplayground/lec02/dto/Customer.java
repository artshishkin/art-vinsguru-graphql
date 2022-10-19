package net.shyshkin.study.graphql.graphqlplayground.lec02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {

    private Integer id;
    private String name;
    private Integer age;
    private String city;

}
