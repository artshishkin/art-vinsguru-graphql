package net.shyshkin.study.graphql.crud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@SpringBootApplication(scanBasePackages = "net.shyshkin.study.graphql.crud.${lec}")
@EnableR2dbcRepositories(basePackages = "net.shyshkin.study.graphql.crud.${lec}")
public class CrudApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrudApplication.class, args);
    }

}
