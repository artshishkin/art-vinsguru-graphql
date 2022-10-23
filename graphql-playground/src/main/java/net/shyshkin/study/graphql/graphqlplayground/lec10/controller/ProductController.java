package net.shyshkin.study.graphql.graphqlplayground.lec10.controller;

import net.shyshkin.study.graphql.graphqlplayground.lec10.dto.Book;
import net.shyshkin.study.graphql.graphqlplayground.lec10.dto.Electronics;
import net.shyshkin.study.graphql.graphqlplayground.lec10.dto.FruitDto;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Controller
@Profile("lec10")
public class ProductController {

    @QueryMapping
    public Flux<Object> products(){
        return Flux.just(
                FruitDto.builder()
                        .description("banana")
                        .price(10)
                        .expiryDate(LocalDate.now().plusDays(14))
                        .build(),
                Electronics.builder()
                        .description("iPhone XS")
                        .brand("APPLE")
                        .price(1000)
                        .build(),
                Book.builder()
                        .description("Lord of Rings")
                        .author("Tolkien")
                        .price(100)
                        .build()
        );
    }

}
