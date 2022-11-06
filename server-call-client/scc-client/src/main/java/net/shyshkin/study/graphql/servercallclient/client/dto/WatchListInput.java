package net.shyshkin.study.graphql.servercallclient.client.dto;

import lombok.Data;

@Data
public class WatchListInput {
    private Integer customerId;
    private Integer movieId;
}
