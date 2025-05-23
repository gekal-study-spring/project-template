-- User test data
INSERT INTO example.users (email,
                           password,
                           name,
                           created_at)
VALUES ('stg.test.user1@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'STG Test User 1',
        CURRENT_TIMESTAMP),
       ('stg.admin@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'STG Admin User',
        CURRENT_TIMESTAMP),
       ('stg.test.user2@example.com',
        '$2a$10$V5j0oeXVqOzJ4jW.qJu4TeQEgN.XZKAhU8MbLf1kmqxW2sQv54cCK',
        'STG Test User 2',
        CURRENT_TIMESTAMP);