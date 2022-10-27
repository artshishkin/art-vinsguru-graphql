package net.shyshkin.study.graphql.client.lec16.clientapp.client;

import lombok.Data;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;

@Data
public class MultiCustomerDto {
    private CustomerDto a;
    private CustomerDto b;
}
