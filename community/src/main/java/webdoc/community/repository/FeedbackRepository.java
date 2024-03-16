package webdoc.community.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import webdoc.community.domain.entity.feedback.Feedback;

/*
 * 피드백 repository
 */
public interface FeedbackRepository extends JpaRepository<Feedback,Long> {
}
