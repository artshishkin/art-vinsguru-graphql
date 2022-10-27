package net.shyshkin.study.graphql.client.lec16.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerEvent {
    private UUID id;
    private CustomerDto customer;
    private Action action;
}
