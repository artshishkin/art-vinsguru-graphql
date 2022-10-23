package net.shyshkin.study.graphql.graphqlplayground.lec09.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.graphql.graphqlplayground.lec09.dto.AllTypes;
import net.shyshkin.study.graphql.graphqlplayground.lec09.dto.Car;
import org.springframework.context.annotation.Profile;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Controller
@Profile("lec09")
public class ScalarController {

    public static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

    @QueryMapping
    public Mono<AllTypes> get() {
        return Mono.just(AllTypes.builder()
                .id(UUID.randomUUID())
                .height(RANDOM.nextInt(100))
                .ageInMonths(Short.valueOf("10"))
                .ageInYears(Byte.valueOf("123"))
                .bigDecimal(BigDecimal.valueOf(RANDOM.nextLong(1_000)))
                .bigInteger(BigInteger.valueOf(RANDOM.nextLong(1_000)))
                .city("Volodymyr")
                .date(LocalDate.now())
                .dateTime(OffsetDateTime.now())
                .time(LocalTime.now())
                .distance(RANDOM.nextLong(1_000_000))
                .car(RANDOM.nextBoolean() ? Car.BMW : Car.HONDA)
                .isValid(RANDOM.nextBoolean())
                .temperature(RANDOM.nextFloat())
                .build());
    }

}
