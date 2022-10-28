package net.shyshkin.study.graphql.client.lec16.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerNotFoundDto implements CustomerResponse{

    private Integer id;
    private String message;

}
