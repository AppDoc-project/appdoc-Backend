package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;

import java.awt.print.Pageable;
import java.util.List;

public interface ThreadRepository extends JpaRepository<Thread,Long> {
    int countThreadByPostId(Long postId);

    @Query("select t from Thread t join t.post where" +
            " t.post.id = :postId and t.parent = null order by t.createdAt asc")
    List<Thread> getThreadByPostId(long postId);
}
