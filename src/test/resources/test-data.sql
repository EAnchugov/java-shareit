-- 1
INSERT into USERS (NAME, email) VALUES ('user1', 'user1@mail.org');
-- 2
INSERT into USERS (NAME, email) VALUES ('user2', 'user2@mail.org');
-- 1
INSERT into items (NAME, DESCRIPTION, available,OWNER)VALUES ('item', 'description', true, 1);

-- 1
INSERT into bookings (START_TIME,END_TIME, ITEM, booker,STATUS) VALUES (NOW() - 1, NOW() + 2, 1, 1, 'APPROVED');
-- 2
INSERT into bookings (START_TIME,END_TIME, ITEM, booker) VALUES (NOW() - 6, NOW() - 1, 1, 1);
-- 3
INSERT into bookings (START_TIME,END_TIME, ITEM, booker) VALUES (NOW() + 6, NOW() + 8, 1, 1);
-- 4
INSERT into bookings (START_TIME,END_TIME, ITEM, booker) VALUES (NOW() - 6, NOW() + 8, 1, 1);