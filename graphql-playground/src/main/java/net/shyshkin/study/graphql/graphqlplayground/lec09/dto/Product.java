package net.shyshkin.study.graphql.graphqlplayground.lec09.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    private String name;
    private Map<String, String> attributes;

}
