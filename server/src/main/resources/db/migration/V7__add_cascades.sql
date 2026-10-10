ALTER TABLE albums DROP CONSTRAINT IF EXISTS fk_albums_home;
ALTER TABLE albums
    ADD CONSTRAINT fk_albums_home
        FOREIGN KEY (home_id) REFERENCES homes(id) ON DELETE CASCADE;

ALTER TABLE album_images DROP CONSTRAINT IF EXISTS fk_album_images_album;
ALTER TABLE album_images
    ADD CONSTRAINT fk_album_images_album
        FOREIGN KEY (album_id) REFERENCES albums(id) ON DELETE CASCADE;