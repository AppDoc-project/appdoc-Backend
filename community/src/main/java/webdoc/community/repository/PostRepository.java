package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.post.Post;

public interface PostRepository extends JpaRepository<Post,Long> {
}
