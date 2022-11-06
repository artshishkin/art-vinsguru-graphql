package net.shyshkin.study.graphql.servercallclient.client.dto;

import lombok.Data;

@Data
public class UserProfile {
    private Integer id;
    private String name;
    private Genre favoriteGenre;
}
