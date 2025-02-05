package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "top")
    private Integer top;

    @Column(name = "leftmost")
    private Integer leftmost;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "isFlippedX")
    private boolean isFlippedX;

    @Column(name = "isFlippedY")
    private boolean isFlippedY;

    @Column(name = "angle")
    private Integer angle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bubble_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Bubble bubble;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private File file;

    @CreatedDate
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Picture(Integer top, Integer leftmost, Integer width, Integer height, Boolean isFlippedX, Boolean isFlippedY, Integer angle, Bubble bubble, File file) {
        this.top = top;
        this.leftmost = leftmost;
        this.width = width;
        this.height = height;
        this.isFlippedX = isFlippedX;
        this.isFlippedY = isFlippedY;
        this.angle = angle;
        this.bubble = bubble;
        this.file = file;
        this.updatedAt = LocalDateTime.now();
    }
}
