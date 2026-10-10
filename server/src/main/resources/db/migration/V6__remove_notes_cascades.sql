ALTER TABLE notes
DROP CONSTRAINT fk_notes_home;

ALTER TABLE notes
    ADD CONSTRAINT fk_notes_home
        FOREIGN KEY (home_id)
            REFERENCES homes(id);
