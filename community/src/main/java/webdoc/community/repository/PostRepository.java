package webdoc.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.community.Community;
import webdoc.community.domain.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query("select distinct p from Post p join fetch p.user " +
            " where p.community = :community")
    Slice<Post> getPostByCommunityAndLimit(Community community, PageRequest pageRequest);

    @Query("select distinct p from Post p join fetch p.user " +
            " where p.community = :community and p.id < :postId")
    Slice<Post> getPostByCommunityAndLimitAndId(Community community,Long postId, PageRequest pageRequest);

    @Query("select p from Post p join fetch p.user " +
            " left join fetch p.pictures where p.id = :id")
    Optional<Post> getCertainPost(Long id);

}
