-- Добавляем поля для OAuth
ALTER TABLE players ADD COLUMN IF NOT EXISTS oauth_provider VARCHAR(50);
ALTER TABLE players ADD COLUMN IF NOT EXISTS oauth_id VARCHAR(255);
ALTER TABLE players ADD COLUMN IF NOT EXISTS avatar_url TEXT;
ALTER TABLE players ADD COLUMN IF NOT EXISTS phone VARCHAR(20);
ALTER TABLE players ADD COLUMN IF NOT EXISTS birthday DATE;
ALTER TABLE players ADD COLUMN IF NOT EXISTS gender VARCHAR(10);

-- Пароль теперь не обязателен (для OAuth пользователей)
ALTER TABLE players ALTER COLUMN password DROP NOT NULL;

-- Уникальность связки провайдер + id
ALTER TABLE players ADD CONSTRAINT unique_oauth UNIQUE (oauth_provider, oauth_id);