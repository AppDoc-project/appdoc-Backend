package webdoc.authentication.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import webdoc.authentication.domain.entity.user.User;
import webdoc.authentication.domain.entity.user.doctor.Doctor;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    @Query("select d from Doctor d where d.id=:id")
    Optional<Doctor> findDoctorById(Long id);

}
