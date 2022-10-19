package net.shyshkin.study.graphql.graphqlplayground.lec02.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgeRangeFilter {

    private Integer minAge;
    private Integer maxAge;

}

