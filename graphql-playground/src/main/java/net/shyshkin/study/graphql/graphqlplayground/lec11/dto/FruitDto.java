package net.shyshkin.study.graphql.graphqlplayground.lec11.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitDto {

    private String description;
    private LocalDate expiryDate;

}
