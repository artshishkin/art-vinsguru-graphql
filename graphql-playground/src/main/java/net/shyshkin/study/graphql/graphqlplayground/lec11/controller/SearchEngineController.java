package net.shyshkin.study.graphql.graphqlplayground.lec11.controller;

import net.shyshkin.study.graphql.graphqlplayground.lec11.dto.Book;
import net.shyshkin.study.graphql.graphqlplayground.lec11.dto.Electronics;
import net.shyshkin.study.graphql.graphqlplayground.lec11.dto.FruitDto;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

@Controller
@Profile("lec11")
public class SearchEngineController {

    private final List<Object> db = List.of(
            FruitDto.builder()
                    .description("banana")
                    .expiryDate(LocalDate.now().plusDays(14))
                    .build(),
            Electronics.builder()
                    .description("iPhone XS")
                    .brand("APPLE")
                    .price(1000)
                    .build(),
            Book.builder()
                    .title("Lord of Rings")
                    .author("Tolkien")
                    .build()
    );

    @QueryMapping
    public Flux<Object> search() {
        return Mono.fromSupplier(() -> new ArrayList<>(db))
                .doOnNext(Collections::shuffle)
                .flatMapIterable(Function.identity());
    }

}
