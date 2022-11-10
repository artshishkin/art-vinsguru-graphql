package net.shyshkin.study.graphql.servercallclient.server.dto;

import lombok.Data;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;

import java.util.List;

@Data
public class ComplexWatchListInput {

    List<WatchListInput> updates;

}
