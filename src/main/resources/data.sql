INSERT INTO
    event (contact, creation_date, description, duration, link, location, name, latitude, longitude, start_date, start_timestamp, end_date, end_timestamp, participant_counter, username, account_id)
VALUES
    ('radek.sala@gmail.com', '2026-03-01 14:00:00.000', 'Meeting description 1', 60, '$2a$04$qOnBrVvVgR0FN/LfX6ChBusEqzJJ1IUhE2GiE4uC25/zzUSGPyHC.', 'Cracow', 'Metting in Cracow', '50.064', '19.945', '2026-03-01', '8:30', '2026-03-30', '18:00', 6, 'Radek', null),
    ('tomek.sapal@gmail.com', '2026-03-01 14:00:05.000', 'Meeting description 2', 60, '$2a$04$qOnBrVvVgR0FN/LfX6ChBusEqzJJ1IUhE2GiE4uC25/zzUSGPyHD.', 'Warsaw', 'Warsaw meeting', '52.241', '21.024', '2026-03-20', '9:10', '2026-03-30', '16:00', 1, 'Tomek', null),
    ('radek.sala@gmail.com', '2026-03-01 14:00:10.000', 'Meeting description 3', 120, '$2a$04$qOnBrVvVgR0FN/LfX6ChBusEqzJJ1IUhE2GiE4uC25/zzUSGPyHE.', 'Niepołomice', 'Niepołomice meeting example', '50.043', '20.221', '2026-04-10', '10:00', '2026-04-10', '20:20', 0, 'Radek', null);

INSERT INTO event_type (name, event_id) VALUES ('EXTENDED', 1);
INSERT INTO event_type (name, event_id) VALUES ('PUBLIC', 2);
INSERT INTO event_type (name, event_id) VALUES ('PRIVATE', 3);

INSERT INTO participation (contact, username, event_id) VALUES ('radek.sala@gmail.com', 'Radek', 1);
INSERT INTO participation (contact, username, event_id) VALUES ('tomek.sapal@gmail.com', 'Tomek', 1);
INSERT INTO participation (contact, username, event_id) VALUES ('tomek.sapal@gmail.com', 'Tomek', 2);

INSERT INTO message (author, date, text, event_id) VALUES ('Radek', '2025-03-05 11:27:31', 'Let''s go', 1);
INSERT INTO message (author, date, text, event_id) VALUES ('Tomek', '2025-03-05 15:12:55', 'Nope this ain''t gonna work!', 1);
INSERT INTO message (author, date, text, event_id) VALUES ('Radek', '2025-04-01 12:00:00', 'message 1', 3);
INSERT INTO message (author, date, text, event_id) VALUES ('Radek', '2025-04-01 12:00:01', 'message 2', 3);
INSERT INTO message (author, date, text, event_id) VALUES ('Radek', '2025-04-01 12:00:02', 'message 3', 3);
INSERT INTO message (author, date, text, event_id) VALUES ('Agata', '2025-03-06 5:45:00', 'I will be available from 2025-03-10 at 10:00 to 2025-03-20 at 15:00.', 1);
INSERT INTO message (author, date, text, event_id) VALUES ('Krzysztof', '2025-03-06 17:30:11', 'In my case, the earliest is 2025-03-12 12:00 and later.', 1);
INSERT INTO message (author, date, text, event_id) VALUES ('Bob', '2025-03-07 14:11:32', 'Sorting this out will be quite a challange, haha.', 1);

INSERT INTO participation (contact, username, event_id) VALUES ('agata@mail.com', 'Agata', 1);
INSERT INTO participation (contact, username, event_id) VALUES ('krzysztof@mail.com', 'Krzysztof', 1);
INSERT INTO participation (contact, username, event_id) VALUES ('bob@mail.com', 'Bob', 1);
INSERT INTO participation (contact, username, event_id) VALUES ('ryszard@mail.com', 'Ryszard', 1);

INSERT INTO intervals (start_date, start_timestamp, end_date, end_timestamp, participation_id)
    VALUES
    ('2026-03-10', '10:00', '2026-03-20', '15:00', 4),
    ('2026-03-12', '12:00', '2026-03-24', '16:00', 5),
    ('2026-03-14', '8:30', '2026-03-18', '14:00', 6),
    ('2026-03-01', '10:00', '2026-03-15', '12:00', 7);