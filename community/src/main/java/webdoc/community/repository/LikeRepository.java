package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Like;
import webdoc.community.domain.entity.post.Post;

import java.util.Optional;

/*
 * 좋아요 repository
 */
public interface LikeRepository extends JpaRepository<Like, Long> {
    // 특정 게시글의 좋아요 개수 반환
    int countLikeByPostId(Long postId);

    // 특정 글에 특정 유저가 남긴 좋아요 반환
    Optional<Like> findLikeByUserIdAndPostId(Long userId,Long postId);
}
