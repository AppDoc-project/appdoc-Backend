package webdoc.authentication.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/*
 * code, message 응답객체
 */
@Getter
@RequiredArgsConstructor
public class CodeMessageResponse {
    private final String message;
    private final int httpStatus;
    private final Integer code;

}
