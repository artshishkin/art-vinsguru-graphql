package net.shyshkin.study.graphql.graphqlplayground.lec10.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FruitDto {

    private final UUID id = UUID.randomUUID();
    private String description;
    private Integer price;
    private LocalDate expiryDate;

}
