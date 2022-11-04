package net.shyshkin.study.graphql.movieapp.dto;

import lombok.Data;

@Data
public class WatchListInput {
    private Integer customerId;
    private Integer movieId;
}
