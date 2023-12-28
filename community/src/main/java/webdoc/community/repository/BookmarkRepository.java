package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.like.Bookmark;

import java.util.List;
import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark,Long> {
    int countBookmarkByPostId(Long postId);

    int countBookmarkByUserId(Long userId);
    Optional<Bookmark> findBookmarkByPostIdAndUserId(Long postId, Long userId);
}
