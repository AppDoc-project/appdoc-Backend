package webdoc.community.domain.entity.tutor.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import webdoc.community.domain.entity.user.Specialities;

import java.util.List;
/*
* 튜터 응답 객체
 */
@Getter
@RequiredArgsConstructor
@Setter
public class TutorResponse {

    private final  String name;
    private final Long id;
    private final List<Specialities> specialities;
    private final String profile;

    private final int reviewCount;
    private  Double score;

    private  Integer lessonCount;

    private  boolean pickYn;

}