CREATE TABLE public.user
(
    uniqueId bigserial NOT NULL,
    email text NOT NULL,
    password text NOT NULL,
    PRIMARY KEY (uniqueId)
);