package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.community.Community;

import java.util.Optional;

/*
 * 커뮤니티 repository
 */
@Repository
public interface CommunityRepository extends JpaRepository<Community,Long> {

    // 커뮤니티 id를 통해 커뉴니티 객체를 가져옴
    Optional<Community> findById(Long id);
}
