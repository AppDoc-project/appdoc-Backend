package webdoc.community.domain.entity.report;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.*;
import webdoc.community.domain.entity.post.Thread;
import webdoc.community.domain.entity.report.request.ReportCreateRequest;
import webdoc.community.repository.ThreadRepository;
/*
 * 댓글 신고 도메인 객체
 */
@Entity
@Getter
@Setter(value = AccessLevel.PRIVATE)
@EqualsAndHashCode(of = "id")
@DiscriminatorValue("thread")
public class ThreadReport extends Report{

    protected ThreadReport(){}

    @Builder
    private ThreadReport(Long userId,String reason, Thread thread){
        super(userId, reason);
        this.thread = thread;
    }
    @OneToOne
    @JoinColumn(nullable = false)
    private Thread thread;

    public static ThreadReport createThreadReport(String reason,Long userId, Thread thread){
        return
                ThreadReport.builder()
                        .userId(userId)
                        .reason(reason)
                        .thread(thread)
                        .build();
    }


}
