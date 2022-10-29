package net.shyshkin.study.graphql.movieapp.dto;

import lombok.Data;

import java.util.List;

@Data
public class WatchListResponse {
    private Status status;
    private List<Integer> watchList;
}
