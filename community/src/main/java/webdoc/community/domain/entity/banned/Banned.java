package webdoc.community.domain.entity.banned;

import jakarta.persistence.*;
import lombok.*;
import webdoc.community.domain.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@EqualsAndHashCode(of = "id")
@Setter(value = AccessLevel.PRIVATE)
public class Banned extends BaseEntity {

    protected Banned(){}
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private Long userId;
    @Column(nullable = false)
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
