ALTER TABLE notes
DROP CONSTRAINT fk_notes_home;

ALTER TABLE notes
    ADD CONSTRAINT fk_notes_home
        FOREIGN KEY (home_id)
            REFERENCES homes(id)
            ON DELETE CASCADE;


ALTER TABLE home_members
DROP CONSTRAINT FKqallih30oy0rv976cib7j3aa8;

ALTER TABLE home_members
    ADD CONSTRAINT fk_home_members_home
        FOREIGN KEY (home_id)
            REFERENCES homes(id)
            ON DELETE CASCADE;