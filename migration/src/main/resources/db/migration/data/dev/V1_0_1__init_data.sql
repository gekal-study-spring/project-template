-- User test data
INSERT INTO example.users (email,
                           password,
                           name,
                           created_at)
VALUES ('dev.test.user1@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'DEV Test User 1',
        CURRENT_TIMESTAMP),
       ('dev.admin@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'DEV Admin User',
        CURRENT_TIMESTAMP),
       ('dev.test.user2@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'DEV Test User 2',
        CURRENT_TIMESTAMP);