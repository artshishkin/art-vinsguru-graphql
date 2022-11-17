package net.shyshkin.study.graphql.servercallclient.server.dto;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Data;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;

import java.util.List;

@Data
public class ComplexWatchListInput {

    @SuppressFBWarnings("EI_EXPOSE_REP")
    private List<WatchListInput> updates;

}
