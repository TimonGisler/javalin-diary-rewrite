insert into public.user (email, "password")
values('test@test.com', 'testpw');

ALTER TABLE entry
    ADD COLUMN creator_id INTEGER REFERENCES public.user(uniqueid)  ON DELETE CASCADE;
