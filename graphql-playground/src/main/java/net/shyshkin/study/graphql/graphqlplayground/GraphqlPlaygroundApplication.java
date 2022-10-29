package net.shyshkin.study.graphql.graphqlplayground;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.shyshkin.study.graphql.graphqlplayground.${lec}")
public class GraphqlPlaygroundApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphqlPlaygroundApplication.class, args);
    }

}
