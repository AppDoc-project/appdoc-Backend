package webdoc.community.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ObjectResponse<T> {
    private final T object;
    private final int httpStatus;
}
