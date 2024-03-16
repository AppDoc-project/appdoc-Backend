package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Bookmark;

import java.util.List;
import java.util.Optional;
/*
 * 북마크 repository
 */
public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    // 특정 게시글의 북마크 개수를 반환
    int countBookmarkByPostId(Long postId);

    // 특정 유저의 북마크 개수를 반환
    int countBookmarkByUserId(Long userId);

    // 특정 글에 특정 유저가 남긴 북마크를 반환
    Optional<Bookmark> findBookmarkByPostIdAndUserId(Long postId, Long userId);
}
