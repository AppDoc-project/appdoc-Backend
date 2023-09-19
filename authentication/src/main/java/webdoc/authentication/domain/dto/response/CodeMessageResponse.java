package webdoc.authentication.domain.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CodeMessageResponse {
    private final String message;
    private final int code;

}
