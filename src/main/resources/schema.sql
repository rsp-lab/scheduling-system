DROP SEQUENCE IF EXISTS account_id_seq;
CREATE SEQUENCE account_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS account
(
    id              integer NOT NULL default nextval('account_id_seq'),
    created_counter integer,
    last_login      timestamp,
    password        varchar(64) NOT NULL,
    register_date   timestamp,
    username        varchar(20),
    enabled boolean NOT NULL,
    unique(id)
);

DROP SEQUENCE IF EXISTS event_id_seq;
CREATE SEQUENCE event_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS event
(
    id                  integer NOT NULL default nextval('event_id_seq'),
    contact             varchar(64),
    creation_date       timestamp,
    description         varchar(256),
    duration            varchar(4),
    end_date            varchar(10),
    end_timestamp       varchar(5),
    latitude            varchar,
    link                varchar(64),
    location            varchar(64),
    longitude           varchar,
    name                varchar(48) NOT NULL,
    participant_counter integer,
    start_date          varchar(10),
    start_timestamp     varchar(5),
    username            varchar(20),
    account_id          integer,
    foreign key (account_id) references account (id),
    unique(id)
);

DROP SEQUENCE IF EXISTS type_id_seq;
CREATE SEQUENCE type_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS event_type
(
    id          integer NOT NULL default nextval('type_id_seq'),
    name        varchar(20) NOT NULL,
    event_id    integer,
    foreign key (event_id) references event (id),
    unique(id)
);

DROP SEQUENCE IF EXISTS role_id_seq;
CREATE SEQUENCE role_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS roles
(
    id      integer NOT NULL default nextval('role_id_seq'),
    name    varchar(20)NOT NULL,
    user_id integer,
    foreign key (user_id) references account (id),
    unique(id)
);

DROP SEQUENCE IF EXISTS participation_id_seq;
CREATE SEQUENCE participation_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS participation
(
    id          integer NOT NULL default nextval('participation_id_seq'),
    contact     varchar(64),
    username    varchar(20),
    event_id    integer,
    foreign key (event_id) references event (id),
    unique(id)
);

DROP SEQUENCE IF EXISTS interval_id_seq;
CREATE SEQUENCE interval_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS intervals
(
    id                  integer NOT NULL default nextval('interval_id_seq'),
    end_date            varchar(10),
    end_timestamp       varchar(5),
    start_date          varchar(10),
    start_timestamp     varchar(5),
    participation_id    integer,
    foreign key (participation_id) references participation (id),
    unique(id)
    );

DROP SEQUENCE IF EXISTS message_id_seq;
CREATE SEQUENCE message_id_seq START WITH 1;
CREATE TABLE IF NOT EXISTS message
(
    id          integer NOT NULL default nextval('message_id_seq'),
    author      varchar(20) NOT NULL,
    date        timestamp,
    text        varchar(256),
    event_id    integer,
    foreign key (event_id) references event (id),
    unique(id)
);
