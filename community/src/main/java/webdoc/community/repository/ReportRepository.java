package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import webdoc.community.domain.entity.report.PostReport;
import webdoc.community.domain.entity.report.Report;
import webdoc.community.domain.entity.report.ThreadReport;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    @Query("select r from ThreadReport r where r.userId =:userId and r.thread.id = :threadId")
    Optional<ThreadReport> findThreadReportByUserIdAndThreadId(Long userId, Long threadId);

    @Query("select r from PostReport  r where r.userId =:userId and r.post.id =:postId")
    Optional<PostReport> findPostReportByUserIdAndPostId(Long userId,Long postId);


}
