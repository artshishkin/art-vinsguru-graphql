package net.shyshkin.study.graphql.client.lec16.serverapp.config;

import graphql.schema.TypeResolver;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerDto;
import net.shyshkin.study.graphql.client.lec16.dto.CustomerNotFoundDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.ClassNameTypeResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

@Configuration
public class TypeResolverConfig {

    @Bean
    RuntimeWiringConfigurer configurer() {
        return c -> c.type("CustomerResponse", b -> b.typeResolver(typeResolver()));
    }

    @Bean
    TypeResolver typeResolver() {
        var typeResolver = new ClassNameTypeResolver();
        typeResolver.addMapping(CustomerDto.class, "Customer");
        typeResolver.addMapping(CustomerNotFoundDto.class, "CustomerNotFound");
        return typeResolver;
    }

}
