package webdoc.authentication.domain.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
public class SubCodeMessageResponse {
    private final String message;
    private final int subCode;
    private final int code;

}
