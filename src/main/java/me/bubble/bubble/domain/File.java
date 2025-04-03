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
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "path")
    private String path;

    @Column(name = "type")
    private String type;

    @Column(name = "size")
    private Double size;

    @CreatedDate
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public File(String path, String type, Double size) {
        this.path = path;
        this.type = type;
        this.size = size;
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String path, String type, Double size) {
        this.path = path;
        this.type = type;
        this.size = size;
    }
}
