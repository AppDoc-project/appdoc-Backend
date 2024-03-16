package webdoc.community.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.post.Post;

import java.util.List;
import java.util.Optional;

/*
 * 게시글 repository
 */
public interface PostRepository extends JpaRepository<Post,Long> {
    // 특정 communityId에 해당하는 게시물을 가져오는 메소드
    @Query(value = "select distinct p from Post p" +
            " join fetch p.community " +
            " where p.community.id = :communityId",
            countQuery = "select count(p) from Post p where p.community = :communityId")
    Slice<Post> getPostByCommunityAndLimit(Long communityId, PageRequest pageRequest);

    // 특정 communityId와 postId 미만의 postId에 해당하는 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndLimitAndId(long communityId, long postId, PageRequest pageRequest);

    // 특정 postId에 해당하는 게시물을 가져오는 메소드
    @Query("select p from Post p" +
            " join fetch p.community " +
            " left join fetch p.pictures where p.id = :id")
    Optional<Post> getCertainPost(long id);

    // 제목에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.title like %:keyword%")
    Slice<Post> getPostByTitleAndLimit(String keyword, PageRequest pageRequest);

    // 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.text like %:keyword% ")
    Slice<Post> getPostByContentAndLimit(String keyword, PageRequest pageRequest);

    // 제목 또는 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.title like %:keyword% and p.text like %:keyword%")
    Slice<Post> getPostByContentAndTitleAndLimit(String keyword, PageRequest pageRequest);

    // 특정 postId 미만의 postId에 해당하고 제목에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.title like %:keyword% and p.id < :postId")
    Slice<Post> getPostByTitleAndLimitAndId(String keyword, Long postId, PageRequest pageRequest);

    // 특정 postId 미만의 postId에 해당하고 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.text like %:keyword% and p.id < :postId")
    Slice<Post> getPostByContentAndLimitAndId(String keyword, Long postId, PageRequest pageRequest);

    // 특정 postId 미만의 postId에 해당하고 제목 또는 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where (p.title like %:keyword% or p.text like %:keyword%) and p.id < :postId")
    Slice<Post> getPostByContentAndTitleAndLimitAndId(String keyword, Long postId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 제목에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.title like %:keyword% and p.community.id = :communityId")
    Slice<Post> getPostByCommunityAndTitleAndLimit(String keyword, Long communityId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.text like %:keyword% and p.community.id = :communityId ")
    Slice<Post> getPostByCommunityAndContentAndLimit(String keyword, Long communityId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 제목 또는 내용에 keyword가 포함된 게시물을 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where (p.title like %:keyword% or p.text like %:keyword%) and p.community.id = :communityId")
    Slice<Post> getPostByCommunityAndContentAndTitleAndLimit(String keyword, Long communityId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 제목에 keyword가 포함된 게시물을 가져오는 메소드 (postId 미만)
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.title like %:keyword% and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndTitleAndLimitAndId(String keyword, Long communityId, Long postId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 내용에 keyword가 포함된 게시물을 가져오는 메소드 (postId 미만)
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where p.text like %:keyword% and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndContentAndLimitAndId(String keyword, Long communityId, Long postId, PageRequest pageRequest);

    // 특정 communityId에 해당하고 제목 또는 내용에 keyword가 포함된 게시물을 가져오는 메소드 (postId 미만)
    @Query("select distinct p from Post p" +
            " join fetch p.community " +
            " where (p.title like %:keyword% or p.text like %:keyword%) and p.community.id = :communityId and p.id < :postId")
    Slice<Post> getPostByCommunityAndContentAndTitleAndLimitAndId(String keyword, Long communityId, Long postId, PageRequest pageRequest);

    // userId에 해당하는 게시물의 수를 가져오는 메소드
    int countPostsByUserId(Long userId);

    // userId에 해당하는 사용자가 작성한 게시물을 id 내림차순으로 가져오는 메소드
    List<Post> getPostByUserIdOrderByIdDesc(Long userId);

    // userId에 해당하는 사용자가 작성한 댓글이 있는 게시물을 id 내림차순으로 가져오는 메소드
    @Query("select distinct p from Post p " +
            " join p.threads t " +
            " where t.userId = :userId order by p.id desc")
    List<Post> getPostsWithMyThread(Long userId);

    // userId에 해당하는 사용자가 북마크한 게시물을 id 내림차순으로 가져오는 메소드
    @Query("select distinct p from Post p" +
            " join p.bookmarks b " +
            " where b.userId = :userId order by p.id desc")
    List<Post> getBookmarkedPosts(Long userId);



}
