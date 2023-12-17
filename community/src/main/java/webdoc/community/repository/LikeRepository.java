package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.Post;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    int countLikeByPostId(Long postId);

    Optional<Like> findLikeByUserIdAndPostId(Long userId,Long postId);
}
