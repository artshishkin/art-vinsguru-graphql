package net.shyshkin.study.graphql.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "net.shyshkin.study.graphql.client.${lec}")
public class GraphQLClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(GraphQLClientApplication.class, args);
    }

}
