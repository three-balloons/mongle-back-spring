package me.bubble.bubble.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

//Entity는 기본 생성자가 있어야 한다!
@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bubble {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "top")
    private int top;

    @Column(name = "leftmost")
    private int leftmost;

    @Column(name = "width")
    private int width;

    @Column(name = "height")
    private int height;

    @Column(name = "path")
    private String path;

    @Column(name = "path_depth")
    private int pathDepth;

    @Column(name = "bubblized")
    private boolean bubblized;

    @Column(name = "visible")
    private boolean visible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Workspace workspace;

    @Builder
    public Bubble(String name, int top, int leftmost, int width, int height, String path, int pathDepth, boolean bubblized, boolean visible, Workspace workspace) {
        this.name = name;
        this.top = top;
        this.leftmost = leftmost;
        this.width = width;
        this.height = height;
        this.path = path;
        this.pathDepth = pathDepth;
        this.bubblized = bubblized;
        this.visible = visible;
        this.workspace = workspace;
    }

    public void update(String name, int top, int leftmost, int width, int height, String path, int pathDepth, boolean bubblized, boolean visible, Workspace workspace) {
        this.name = name;
        this.top = top;
        this.leftmost = leftmost;
        this.width = width;
        this.height = height;
        this.path = path;
        this.pathDepth = pathDepth;
        this.bubblized = bubblized;
        this.visible = visible;
        this.workspace = workspace;
    }
}
