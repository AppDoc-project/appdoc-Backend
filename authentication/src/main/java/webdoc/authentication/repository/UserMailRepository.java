package webdoc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.UserMail;

import java.util.Optional;
/*
* 유저 회원가입 repository
 */
@Repository
public interface UserMailRepository extends JpaRepository<UserMail,Long> {
    // email을 통해 userMail정보를 가져옴
    Optional<UserMail> findByEmail(String email);
}
