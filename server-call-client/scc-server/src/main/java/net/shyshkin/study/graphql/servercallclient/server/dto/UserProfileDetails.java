package net.shyshkin.study.graphql.servercallclient.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.shyshkin.study.graphql.servercallclient.common.dto.Movie;
import net.shyshkin.study.graphql.servercallclient.common.dto.UserProfile;

import java.util.List;

@SuppressFBWarnings("EI_EXPOSE_REP")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProfileDetails extends UserProfile {
    private List<Movie> watchList;
    private List<Movie> recommended;
}
