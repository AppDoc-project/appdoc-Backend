package webdoc.authentication.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * 객체 응답객체
 */
@Getter
@RequiredArgsConstructor
public class ObjectResponse<T> {
    private final T object;
    private final int httpStatus;
}
