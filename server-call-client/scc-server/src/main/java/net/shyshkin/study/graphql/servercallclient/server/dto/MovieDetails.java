package net.shyshkin.study.graphql.servercallclient.server.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.common.dto.Review;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class MovieDetails extends Movie {
    List<Review> reviews;
}
