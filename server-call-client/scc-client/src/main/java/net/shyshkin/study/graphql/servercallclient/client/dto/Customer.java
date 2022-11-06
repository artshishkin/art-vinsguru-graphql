package net.shyshkin.study.graphql.servercallclient.client.dto;

import lombok.Data;

import java.util.List;

@Data
public class Customer {
    private Integer id;
    private String name;
    private Genre favoriteGenre;
    private List<Integer> watchList;
}
