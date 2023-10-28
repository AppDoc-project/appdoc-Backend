package webdoc.community.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CodeMessageResponse {
    private final String message;
    private final int httpStatus;
    private final Integer code;

}
