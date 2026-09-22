package com.lords.server.album.entity;

import com.lords.server.media.entity.Media;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "album_images")
public class AlbumImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @ManyToOne
    @JoinColumn(name = "media_id", nullable = false)
    private Media media;

}
