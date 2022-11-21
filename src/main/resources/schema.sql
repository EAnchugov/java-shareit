DROP ALL OBJECTS;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255) NOT NULL,
                                     email VARCHAR(512) NOT NULL,
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email),
                                     CONSTRAINT pk_user PRIMARY KEY (id)

);
CREATE TABLE items(
                      id bigint generated by default as identity not null,
                      name varchar not null,
                      description varchar not null,
                      available varchar not null,
                      owner bigint references USERS(id) on delete cascade,
                      request bigint,
                      CONSTRAINT pk_item PRIMARY KEY (id)
);
CREATE TABLE bookings(
                         id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                         start_time TIMESTAMP WITHOUT TIME ZONE,
                         end_time TIMESTAMP WITHOUT TIME ZONE,
                         item BIGINT references ITEMS(id),
                         booker BIGINT references USERS(id),
                         status VARCHAR,
                        CONSTRAINT pk_booking PRIMARY KEY (id)
);



CREATE TABLE comments (
                          id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                          comment_text varchar not null,
                          item bigint references ITEMS(id),
                          author BIGINT references USERS(id) ,
                          created TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS requests
(
    id          BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description VARCHAR                                 NOT NULL,
    requestor   BIGINT references USERS (id),
    created     TIMESTAMP WITHOUT TIME ZONE
);