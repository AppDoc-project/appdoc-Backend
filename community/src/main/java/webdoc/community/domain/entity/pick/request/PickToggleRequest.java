package webdoc.community.domain.entity.pick.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
/*
* 강사 찜하기 생성 객체
 */
@Getter
@Setter
public class PickToggleRequest {
    @NotNull
    private Long tutorId;
}
