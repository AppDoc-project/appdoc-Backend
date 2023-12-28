package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.banned.Banned;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface BannedRepository  extends JpaRepository<Banned,Long> {
    Optional<Banned> findBannedByUserIdAndUntilWhenAfter(Long userId, LocalDateTime untilWhen);
}
