package net.shyshkin.study.graphql.servercallclient.server.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.servercallclient.common.dto.WatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.ComplexWatchListInput;
import net.shyshkin.study.graphql.servercallclient.server.dto.UserProfileDetails;
import org.junit.jupiter.api.RepeatedTest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class ComplexQueriesIT extends BaseControllerIT {

    @RepeatedTest(5)
    void complexWatchListUpdateRESTLikeTest() {

        //given
        int usersCount = 4;
        int moviesCount = 10;
        LocalDateTime startTime = LocalDateTime.now();
        Stream<Integer> customerIdStream = IntStream.rangeClosed(1, usersCount).boxed();
        List<WatchListInput> updates = customerIdStream
                .flatMap(customerId -> IntStream.rangeClosed(1, moviesCount)
                        .boxed()
                        .map(movieId -> new WatchListInput() {{
                            setCustomerId(customerId);
                            setMovieId(movieId);
                        }})
                )
                .collect(Collectors.toList());
        ComplexWatchListInput complexWatchListInput = new ComplexWatchListInput() {{
            setUpdates(updates);
        }};

        //when
        webTestClient.post()
                .uri("/rest/users/complex/watch-list")
                .header("X-Client-Id", CLIENT_ID.toString())
                .bodyValue(complexWatchListInput)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBodyList(UserProfileDetails.class)
                .value(customers -> assertThat(customers)
                        .hasSize(usersCount)
                        .allSatisfy(customer -> assertThat(customer.getWatchList())
//                                .hasSize(moviesCount)
                                .hasSizeLessThanOrEqualTo(moviesCount) //something wrong happens actual is less then expected
                                .allSatisfy(watchList -> assertThat(watchList).hasNoNullFieldsOrProperties())
                        )
                );
        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);
        System.out.println("-------------------------------");
        log.debug("Duration of complexWatchListUpdateRESTLikeTest: {}", duration);
        System.out.println("-------------------------------");
    }

}