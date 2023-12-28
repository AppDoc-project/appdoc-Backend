package webdoc.community.domain.entity.banned;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Setter(value = AccessLevel.PRIVATE)
public class Banned {

    protected Banned(){}
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private Long userId;
    @Column
    private LocalDateTime untilWhen;
    @Column
    private String reason;
    @Builder
    private Banned(Long userId, LocalDateTime untilWhen,String reason){
        this.userId = userId;
        this.untilWhen = untilWhen;
        this.reason = reason;
    }

    public static Banned createBanned(Long userId, LocalDateTime untilWhen,String reason){
        return
                Banned
                        .builder()
                        .untilWhen(untilWhen)
                        .userId(userId)
                        .reason(reason)
                        .build();
    }





}
