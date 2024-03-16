package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import webdoc.community.domain.entity.banned.Banned;

import java.time.LocalDateTime;
import java.util.Optional;
/*
 * 정지 repository : deprecated
 */
@Repository
public interface BannedRepository  extends JpaRepository<Banned,Long> {
    // 특정 기간 까지 금지된 유저를 가져온다
    Optional<Banned> findBannedByUserIdAndUntilWhenAfter(Long userId, LocalDateTime untilWhen);
}
