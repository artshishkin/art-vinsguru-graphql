package net.shyshkin.study.graphql.client.lec16.clientapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.graphql.ResponseError;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class GenericResponse<T> {

    private T data;
    private ResponseError error;

    public boolean isDataPresent() {
        return data != null;
    }
}
