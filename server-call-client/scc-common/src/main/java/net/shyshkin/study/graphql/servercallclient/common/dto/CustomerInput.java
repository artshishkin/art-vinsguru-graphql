package net.shyshkin.study.graphql.servercallclient.common.dto;

import lombok.Data;

@Data
public class CustomerInput {
    private Integer id;
    private String name;
    private Genre favoriteGenre;
}
