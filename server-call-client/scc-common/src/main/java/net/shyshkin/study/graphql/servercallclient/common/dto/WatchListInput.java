package net.shyshkin.study.graphql.servercallclient.common.dto;

import lombok.Data;

@Data
public class WatchListInput {
    private Integer customerId;
    private Integer movieId;
}
