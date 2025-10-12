INSERT INTO roles (role_name)
VALUES ('USER'), ('ADMIN')
    ON CONFLICT (role_name) DO NOTHING;

INSERT INTO users (username, password, email, first_name, last_name, date_of_birth, address, phone_number)
VALUES
    ('admin', '$2a$10$u2iQ0jGm7D0iBzC6sWp.8uEq.R9jT7soOglA.xzgc8P02poGRpYQG', 'admin@jobexchange.com', 'System', 'Admin', '1990-01-01', 'Moscow', '+79998887766'),
    ('jdoe', '$2a$10$P8uVpkV5YcUNsNjSmZT0CuBGmto2xBBMc6SkK2q0jlW4sfD9eQbAa', 'jdoe@gmail.com', 'John', 'Doe', '1995-06-15', 'Saint Petersburg', '+79001112233');

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE (u.username = 'admin' AND r.role_name = 'ADMIN')
   OR (u.username = 'jdoe' AND r.role_name = 'USER');
