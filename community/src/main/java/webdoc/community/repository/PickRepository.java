package webdoc.community.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.pick.Pick;

import java.util.List;
/*
 * 찜 repository
 */
public interface PickRepository  extends JpaRepository<Pick, Long> {
    // 특정 튜티가 튜터를 찜한 것 반환
    Pick findByTutorIdAndTuteeId(Long tutorId,Long tuteeId);

    // 특정 튜터의 찜 개수 반환
    int countByTutorId(Long tutorId);

    // 특정 튜티의 찜 개수 반환
    int countByTuteeId(Long tuteeId);

    // 튜티가 했던 모든 찜 반환
    List<Pick> findByTuteeId(Long tuteeId);



}
