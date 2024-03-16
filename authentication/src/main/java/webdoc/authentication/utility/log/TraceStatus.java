//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package webdoc.authentication.utility.log;

/*
 * 함수별 로그를 담고있는 객체
 */
public class TraceStatus {
    private TraceId traceId;
    private Long startTimeMs;
    private String message;

    public TraceStatus(TraceId traceId, Long startTimeMs, String message) {
        this.traceId = traceId;
        this.startTimeMs = startTimeMs;
        this.message = message;
    }

    public Long getStartTimeMs() {
        return this.startTimeMs;
    }

    public String getMessage() {
        return this.message;
    }

    public TraceId getTraceId() {
        return this.traceId;
    }
}
