package net.shyshkin.study.graphql.movieapp.dto;

import lombok.Data;

@Data
public class CustomerInput {
    private Integer id;
    private String name;
    private Genre favoriteGenre;
}
