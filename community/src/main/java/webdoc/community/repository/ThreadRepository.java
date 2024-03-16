package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Thread;
import java.util.List;
import java.util.Optional;
/*
 * 댓글 repository
 */
public interface ThreadRepository extends JpaRepository<Thread,Long> {
    // 특정 게시글의 댓글 개수를 센다
    int countThreadByPostId(Long postId);

    // 특정 게시글의 댓글을 가져온다
    @Query("select t from Thread t join t.post where" +
            " t.post.id = :postId and t.parent = null order by t.createdAt asc")
    List<Thread> getThreadByPostId(long postId);

    // 특정 id를 가진 댓글을 가져온다
    Optional<Thread> getThreadById(Long id);

    // 특정안이 작성한 댓글 개수를 가져온다
    int countThreadByUserId(Long userId);
}
