package net.shyshkin.study.graphql.graphqlplayground.lec05_01.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {

    private UUID id;
    private Integer amount;
    private AccountType accountType;

}
