package webdoc.authentication.utility.log;

import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Async;

import java.util.ArrayList;
import java.util.List;

/*
 * 요청당 함수별 실행시간을 기록하는 객체
 */
public class LogTrace {
    private final Logger logger;
    private static final String START_PREFIX = "-->";
    private static final String COMPLETE_PREFIX = "<--";
    private static final String EX_PREFIX = "<--";
    private ThreadLocal<TraceId> traceIdHolder = new ThreadLocal();
    private ThreadLocal<List<String>> logs = new ThreadLocal();

    public LogTrace(Logger logger) {
        this.logger = logger;
    }

    public TraceStatus begin(String message) {
        this.syncTraceId();
        this.makeLogs();
        TraceId traceId = this.traceIdHolder.get();
        Long startTimeMs = System.currentTimeMillis();
        List<String> logList = this.logs.get();
        logList.add("[" + traceId.getId() + "] " + this.addSpace("-->", traceId.getLevel()) + message);
        return new TraceStatus(traceId, startTimeMs, message);
    }

    public void end(TraceStatus status) {
        this.complete(status, null);
    }

    public void exception(TraceStatus status, Exception e) {
        this.complete(status, e);
    }

    @Async
    public synchronized void complete(TraceStatus status, Exception e) {
        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        TraceId traceId = status.getTraceId();
        List<String> logList = this.logs.get();
        if (e == null) {
            logList.add("[" + traceId.getId() + "] " + this.addSpace("<--", traceId.getLevel()) + status.getMessage() + "times=" + resultTimeMs + "ms");
        } else {
            logList.add("[" + traceId.getId() + "] " + this.addSpace("<--", traceId.getLevel()) + status.getMessage() + "times=" + resultTimeMs + "ms ex=" + e.toString());
        }
        this.release();
    }

    private String addSpace(String prefix, int level) {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < level; ++i) {
            sb.append(i == level - 1 ? "|" + prefix : "|  ");
        }

        return sb.toString();
    }

    private void release() {
        TraceId traceId = this.traceIdHolder.get();
        if (traceId.isFirstLevel()) {
            this.traceIdHolder.remove();
            this.outputLogsToFile();
        } else {
            this.traceIdHolder.set(traceId.createPreviousId());
        }
    }

    private void makeLogs() {
        List<String> log = this.logs.get();
        if (log == null) {
            this.logs.set(new ArrayList());
        }
    }

    private void syncTraceId() {
        TraceId traceId = this.traceIdHolder.get();
        if (traceId == null) {
            this.traceIdHolder.set(new TraceId());
        } else {
            this.traceIdHolder.set(traceId.createNextId());
        }
    }

    private void outputLogsToFile() {
        logger.info(String.join("\n",logs.get()));
    }
}
