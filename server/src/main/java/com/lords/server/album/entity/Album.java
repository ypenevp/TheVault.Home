package com.lords.server.album.entity;

import com.lords.server.home.entity.Home;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@Table(name = "albums", uniqueConstraints = @UniqueConstraint(name = "uq_albums_home_name", columnNames = {"home_id", "name"}))
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "home_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Home home;
}
