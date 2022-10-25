package net.shyshkin.study.graphql.crud.lec14.entity;

import lombok.*;
import org.springframework.data.annotation.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @Id
    @EqualsAndHashCode.Include
    private Integer id;
    private String name;
    private Integer age;
    private String city;

}
