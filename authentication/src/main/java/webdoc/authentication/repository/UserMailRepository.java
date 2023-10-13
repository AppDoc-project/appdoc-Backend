package webdoc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.UserMail;

import java.util.Optional;

@Repository
public interface UserMailRepository extends JpaRepository<UserMail,Long> {
    Optional<UserMail> findByEmail(String email);
}
