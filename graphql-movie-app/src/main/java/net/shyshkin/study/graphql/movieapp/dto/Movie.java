package net.shyshkin.study.graphql.movieapp.dto;

import lombok.Data;

@Data
public class Movie {
    private Integer id;
    private String title;
    private Integer releaseYear;
    private Genre genre;
    private Double rating;
}
