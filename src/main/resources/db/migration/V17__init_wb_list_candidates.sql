-- af.wb_list_candidate --
CREATE TABLE af.wb_list_candidate
(
    id          BIGSERIAL         NOT NULL,
    party_id    CHARACTER VARYING,
    shop_id     CHARACTER VARYING,
    list_type   af.list_type      NOT NULL,
    list_name   CHARACTER VARYING NOT NULL,
    value       CHARACTER VARYING NOT NULL,
    source      CHARACTER VARYING NOT NULL,
    approved    BOOLEAN           NOT NULL,
    insert_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),
    update_time TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT (now() at time zone 'utc'),

    CONSTRAINT wb_list_candidate_pkey PRIMARY KEY (id),
    UNIQUE (list_name, value)
);
