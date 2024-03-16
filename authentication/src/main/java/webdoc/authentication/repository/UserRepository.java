package webdoc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.tutor.Tutor;

import java.util.List;
import java.util.Optional;

/*
 * 유저 repository
 */
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    // email을 통한 유저 검색
    Optional<User> findByEmail(String email);

    // id로 튜터 검색
    @Query("select t from Tutor t where t.id=:id")

    Optional<Tutor> findTutorById(Long id);
    // id로 유저 검색
    Optional<User> findUserById(Long id);

    // email로 유저 검색
    Optional<User> findUserByEmail(String email);

    // 이름으로 튜터 검색
    @Query("select t from Tutor t where t.name=:name")
    List<Tutor> findTutorByName(String name);

}

