package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.post.Post;
import webdoc.community.domain.entity.post.Thread;

public interface ThreadRepository extends JpaRepository<Thread,Long> {
    int countThreadByPostId(Long postId);
}
