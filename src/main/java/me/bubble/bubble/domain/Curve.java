package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Curve {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "color")
    private String color;

    @Column(name = "thickness")
    private Integer thickness;

    @Column(name = "control_point")
    private String controlPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bubble_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Bubble bubble;

    @CreatedDate
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public Curve(String color, Integer thickness, Bubble bubble, String controlPoint) {
        this.color = color;
        this.thickness = thickness;
        this.bubble = bubble;
        this.controlPoint = controlPoint;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String color, Integer thickness, Bubble bubble, String controlPoint) {
        this.color = color;
        this.thickness = thickness;
        this.bubble = bubble;
        this.controlPoint = controlPoint;
    }
}
