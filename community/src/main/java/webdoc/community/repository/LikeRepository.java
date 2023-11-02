package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.Post;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countLikeByPostId(Long postId);
}
