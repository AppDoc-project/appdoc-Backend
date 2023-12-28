package webdoc.community.domain.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CountResponse {
    private final int postCount;

    private final int threadCount;

    private final int bookmarkCount;
}
