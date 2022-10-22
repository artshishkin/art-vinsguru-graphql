package net.shyshkin.study.graphql.graphqlplayground.lec07.config;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.graphql.graphqlplayground.lec07.service.CustomerOrderDataFetcher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
@Profile("lec07")
@RequiredArgsConstructor
public class DataFetcherWiringConfig {

    private final CustomerOrderDataFetcher customerOrderDataFetcher;

    @Bean
    public RuntimeWiringConfigurer configurer() {
        return c -> c.type(
                "Query",
                b -> b.dataFetcher("customers", customerOrderDataFetcher)
        );
    }

}
