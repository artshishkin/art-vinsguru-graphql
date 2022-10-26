package net.shyshkin.study.graphql.errorhandling.lec15.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {

    private Integer id;
    @NotBlank(message = "Name must not be empty")
    private String name;
    @Min(value = 18L, message = "Age must be greater then 18")
    private Integer age;
    private String city;

}
