package net.shyshkin.study.graphql.servercallclient.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WatchListResponse {
    private Status status;
    private List<Integer> watchList;
}
