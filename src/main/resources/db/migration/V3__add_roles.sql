-- V3__add_roles.sql

CREATE TABLE IF NOT EXISTS roles (
                                     id BIGSERIAL PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(200)
    );

CREATE TABLE IF NOT EXISTS player_roles (
                                            player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
    role_id BIGINT NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (player_id, role_id)
    );

-- Роли
INSERT INTO roles (name, description) VALUES ('ROLE_USER', 'Обычный пользователь') ON CONFLICT (name) DO NOTHING;
INSERT INTO roles (name, description) VALUES ('ROLE_ADMIN', 'Администратор') ON CONFLICT (name) DO NOTHING;

-- Всем существующим — ROLE_USER
INSERT INTO player_roles (player_id, role_id)
SELECT p.id, r.id
FROM players p, roles r
WHERE r.name = 'ROLE_USER'
  AND NOT EXISTS (
    SELECT 1 FROM player_roles pr
    WHERE pr.player_id = p.id AND pr.role_id = r.id
);

-- Админу — ROLE_ADMIN
INSERT INTO player_roles (player_id, role_id)
SELECT p.id, r.id
FROM players p, roles r
WHERE p.email = 'evgenypavlov666@yandex.ru' AND r.name = 'ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM player_roles pr
    WHERE pr.player_id = p.id AND pr.role_id = r.id
);