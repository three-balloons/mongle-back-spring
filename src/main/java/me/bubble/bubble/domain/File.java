package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File {
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

    @Column(name = "path")
    private String path;

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

    @Builder
    public File(Integer top, Integer leftmost, Integer width, Integer height, String path, Boolean isFlippedX, Boolean isFlippedY, Integer angle, Bubble bubble) {
        this.top = top;
        this.leftmost = leftmost;
        this.width = width;
        this.height = height;
        this.path = path;
        this.isFlippedX = isFlippedX;
        this.isFlippedY = isFlippedY;
        this.angle = angle;
        this.bubble = bubble;
    }

    public void update(Integer top, Integer leftmost, Integer width, Integer height,  String path, Boolean isFlippedX, Boolean isFlippedY, Integer angle, Bubble bubble) {
        this.top = top;
        this.leftmost = leftmost;
        this.width = width;
        this.height = height;
        this.path = path;
        this.isFlippedX = isFlippedX;
        this.isFlippedY = isFlippedY;
        this.angle = angle;
        this.bubble = bubble;
    }

}
