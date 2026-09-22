package com.lords.server.album.entity;

import com.lords.server.home.entity.Home;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    private Home home;
}
