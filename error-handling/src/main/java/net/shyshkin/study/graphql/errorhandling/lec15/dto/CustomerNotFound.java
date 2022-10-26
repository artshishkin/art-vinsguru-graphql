package net.shyshkin.study.graphql.errorhandling.lec15.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerNotFound {

    private Integer id;
    @Builder.Default
    private String message = "user not found";

}
