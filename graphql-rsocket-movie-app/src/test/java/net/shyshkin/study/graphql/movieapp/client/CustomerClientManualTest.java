package net.shyshkin.study.graphql.movieapp.client;

import net.shyshkin.study.graphql.movieapp.dto.CustomerInput;
import net.shyshkin.study.graphql.movieapp.dto.Genre;
import net.shyshkin.study.graphql.movieapp.dto.WatchListInput;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Disabled("Only for manual testing. Run `external-services` first: `docker-compose up`")
class CustomerClientManualTest {

    @Autowired
    CustomerClient customerClient;

    @Test
    @DisplayName("Requesting customer by id (PRESENT) should return correct customer")
    void getCustomerById_presentTest() {

        //given
        Integer id = 1;

        //when
        customerClient.getCustomerById(id)

                //then
                .as(StepVerifier::create)
                .consumeNextWith(customer -> assertAll(
                        () -> assertThat(customer).hasNoNullFieldsOrProperties(),
                        () -> assertThat(customer.getId()).isEqualTo(id))
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Requesting customer by id (ABSENT) should return Complete signal")
    void getCustomerById_absentTest() {

        //given
        Integer id = 3000;

        //when
        customerClient.getCustomerById(id)

                //then
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("Updating PRESENT customer should return updated customer")
    void updateCustomer_presentTest() {

        //given
        Integer id = 1;
        CustomerInput customerInput = new CustomerInput() {{
            setId(id);
            setName("art");
            setFavoriteGenre(Genre.FANTASY);
        }};

        //when
        customerClient.updateCustomer(customerInput)

                //then
                .as(StepVerifier::create)
                .consumeNextWith(customer -> assertAll(
                                () -> assertThat(customer).hasNoNullFieldsOrProperties(),
                                () -> assertThat(customer.getName()).isEqualTo("art"),
                                () -> assertThat(customer.getFavoriteGenre()).isEqualTo(Genre.FANTASY)
                        )
                )
                .verifyComplete();
    }

    @Test
    @DisplayName("Updating ABSENT customer should return Complete signal")
    void updateCustomer_absentTest() {

        //given
        Integer id = 1000;
        CustomerInput customerInput = new CustomerInput() {{
            setId(id);
            setName("art");
            setFavoriteGenre(Genre.FANTASY);
        }};

        //when
        customerClient.updateCustomer(customerInput)

                //then
                .as(StepVerifier::create)
                .verifyComplete();
    }

    @Test
    @DisplayName("Adding Movie to a watchlist (PRESENT customer) should return updated watchlist with indexes")
    void addMovieToCustomerWatchlist_presentTest() {

        //given
        Integer id = 1;
        int movieId = 100;
        WatchListInput watchListInput = new WatchListInput() {{
            setCustomerId(id);
            setMovieId(movieId);
        }};

        //when
        customerClient.addMovieToCustomerWatchlist(watchListInput)

                //then
                .collectList()
                .as(StepVerifier::create)
                .consumeNextWith(movieIds -> assertThat(movieIds).contains(movieId))
                .verifyComplete();
    }

    @Test
    @DisplayName("Adding Movie to a watchlist (ABSENT customer) should return watchlist with only 1 movie id???")
    void addMovieToCustomerWatchlist_absentTest() {

        //given
        Integer id = 1000;
        int movieId = 100;
        WatchListInput watchListInput = new WatchListInput() {{
            setCustomerId(id);
            setMovieId(movieId);
        }};

        //when
        customerClient.addMovieToCustomerWatchlist(watchListInput)

                //then
                .as(StepVerifier::create)
                .expectNext(movieId)
                .verifyComplete();
    }

}