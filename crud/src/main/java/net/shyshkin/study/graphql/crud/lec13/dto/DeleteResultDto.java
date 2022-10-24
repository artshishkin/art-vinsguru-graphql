package net.shyshkin.study.graphql.crud.lec13.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeleteResultDto {

    private Integer id;
    private Status status;

}
