package webdoc.authentication.domain.entity.user.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import webdoc.authentication.domain.entity.user.tutor.enums.Specialities;

import java.util.List;
/*
* 다른 서비스에게 인증 정보 응답 객체
 */
@Getter
@Setter
public class UserResponse {
    private Long id;
    private String email;
    private String nickName;
    private String name;
    private Boolean isTutor;
    private String contact;
    private String profile;
    private String selfDescription;
    private String role;
    private List<Specialities> specialities;
    @Builder
    private UserResponse(Long id, String email, String nickName, String name,
                         Boolean isTutor, String contact, String profile,
                         String selfDescription, List<Specialities> specialities, String role){
        this.id = id;
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.isTutor = isTutor;
        this.contact = contact;
        this.profile = profile;
        this.selfDescription = selfDescription;
        this.specialities = specialities;
        this.role = role;

    }



}
