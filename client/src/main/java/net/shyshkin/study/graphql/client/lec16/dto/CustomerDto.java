package net.shyshkin.study.graphql.client.lec16.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerDto implements CustomerResponse{

    private Integer id;
    private String name;
    private Integer age;
    private String city;

}
