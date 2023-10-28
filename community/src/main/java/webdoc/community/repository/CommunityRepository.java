package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.community.Community;

import java.util.Optional;

@Repository
public interface CommunityRepository extends JpaRepository<Community,Long> {
    Optional<Community> findById(Long id);
}
