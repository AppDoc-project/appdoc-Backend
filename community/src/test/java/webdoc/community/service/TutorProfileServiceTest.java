package webdoc.community.service;

import static org.assertj.core.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import webdoc.community.domain.entity.pick.Pick;
import webdoc.community.domain.entity.user.Specialities;
import webdoc.community.domain.entity.user.response.UserResponse;
import webdoc.community.repository.PickRepository;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TutorProfileServiceTest {
    @MockBean
    UserService userService;
    @Autowired
    TutorProfileService tutorProfileService;

    @Autowired
    PickRepository pickRepository;

    @DisplayName("튜터 찜하기를 테스트한다")
    @Test
    void tutorPickToggle(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(true))
                );


        //when
        tutorProfileService.toggleTutor(1L,2L);

        //then
        Pick pick = pickRepository.findByTutorIdAndTuteeId(2L,1L);
        assertThat(pick).isNotNull();
     }

    @DisplayName("튜터 찜하기를 두번 하면 찜이 취소된다")
    @Test
    void tutorPickToggleTwice(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(true))
                );


        //when
        tutorProfileService.toggleTutor(1L,2L);
        tutorProfileService.toggleTutor(1L,2L);
        Pick pick = pickRepository.findByTutorIdAndTuteeId(2L,1L);

        //then
        assertThat(pick).isNull();
    }

    @DisplayName("튜티를 찜할 수는 없다")
    @Test
    void tutorPickToggleWithTutee(){
        //given
        when(userService.fetchUserResponseFromAuthServer(any(),any(),any()))
                .thenReturn(
                        Optional.of(createUser(false))
                );


        //when + then
        assertThatThrownBy(()->      tutorProfileService.toggleTutor(1L,2L))
                .isInstanceOf(IllegalStateException.class);

    }

    public UserResponse createUser(boolean isTutor){
        return UserResponse.builder()
                .contact("01025045779")
                .email("1dilumn0@gmail.com")
                .id(2L)
                .isTutor(isTutor)
                .nickName("우석우")
                .name("우석우")
                .profile("naver.com")
                .specialities(List.of(Specialities.KEYBOARD_INSTRUMENT))
                .build();
    }
}
