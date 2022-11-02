package net.shyshkin.study.graphql.movieapp.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ReactiveKeycloakAuthorityConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

    @Override
    public Flux<GrantedAuthority> convert(Jwt jwt) {
        ConcurrentLinkedQueue<GrantedAuthority> authorities = new ConcurrentLinkedQueue<>();
        Flux<GrantedAuthority> roleBasedAuthorities = Flux.fromIterable(getRoleBasedAuthorities(jwt));
        Flux<GrantedAuthority> scopeBasedAuthorities = Flux.fromIterable(getScopeBasedAuthorities(jwt));
        return Flux.merge(roleBasedAuthorities, scopeBasedAuthorities)
                .doOnNext(authorities::add)
                .doOnComplete(() -> log.debug("Convert to granted authorities: {}", authorities));
    }

    private List<GrantedAuthority> getRoleBasedAuthorities(Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        return Optional
                .ofNullable(realmAccess)
                .map(rAccess -> rAccess.get("roles"))
                .filter(roles -> roles instanceof List)
                .map(roles -> (List<String>) roles)
                .stream()
                .flatMap(Collection::stream)
                .map("ROLE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }

    private List<GrantedAuthority> getScopeBasedAuthorities(Jwt jwt) {
        String scopeField = jwt.getClaimAsString("scope");
        return Optional
                .ofNullable(scopeField)
                .filter(Strings::isNotBlank)
                .map(scopes -> scopes.split(" "))
                .stream()
                .flatMap(Stream::of)
                .filter(Strings::isNotBlank)
                .map("SCOPE_"::concat)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toUnmodifiableList());
    }
}
