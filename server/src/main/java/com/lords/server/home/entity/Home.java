package com.lords.server.home.entity;

import com.lords.server.auth.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@Table(name = "homes")
public class Home {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner", nullable = false)
    private User owner;

    @Column(name = "size", nullable = false)
    private Long totalSizeInBytes;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdAt;

    @Column(name = "max_size", nullable = false)
    private Long maxSizeInBytes;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDate.now();
        this.maxSizeInBytes = 5000000000L;
    }
}





