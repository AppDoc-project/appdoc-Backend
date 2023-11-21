package webdoc.community.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Post;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post,Long> {
    @Query(value = "select distinct p from Post p" +
            " where p.community.id = :communityId"
            ,countQuery = "select count(p) from Post p where p.community = :communityId")
    Slice<Post> getPostByCommunityAndLimit(Long communityId, PageRequest pageRequest);

    @Query("select distinct p from Post p" +
            " where p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndLimitAndId(long communityId,long postId,PageRequest pageRequest);

    @Query("select p from Post p" +
            " left join fetch p.pictures where p.id = :id")
    Optional<Post> getCertainPost(long id);

}
