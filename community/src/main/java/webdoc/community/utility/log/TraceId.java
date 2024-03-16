//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package webdoc.community.utility.log;

import java.util.UUID;

/*
 * 함수별 로그를 트레이싱하는 객체
 */
public class TraceId {
    private String id;
    private int level;

    public TraceId() {
        this.id = this.createId();
        this.level = 0;
    }

    public TraceId(String id, int level) {
        this.id = id;
        this.level = level;
    }

    private String createId() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    public TraceId createNextId() {
        return new TraceId(this.id, this.level + 1);
    }

    public TraceId createPreviousId() {
        return new TraceId(this.id, this.level - 1);
    }

    public boolean isFirstLevel() {
        return this.level == 0;
    }

    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }
}
