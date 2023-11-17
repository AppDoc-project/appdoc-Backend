package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.user.User;
import webdoc.community.domain.entity.user.tutor.Tutor;


import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query("select t from Tutor t where t.id=:id")
    Optional<Tutor> findTutorById(Long id);

}
