package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Blob;
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
    private int thickness;

    @Column(name = "control_point")
    private String controlPoint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bubble_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Bubble bubble;

    @Builder
    public Curve(String color, int thickness, Bubble bubble, String controlPoint) {
        this.color = color;
        this.thickness = thickness;
        this.bubble = bubble;
        this.controlPoint = controlPoint;
    }

    public void update(String color, int thickness, Bubble bubble, String controlPoint) {
        this.color = color;
        this.thickness = thickness;
        this.bubble = bubble;
        this.controlPoint = controlPoint;
    }
}
