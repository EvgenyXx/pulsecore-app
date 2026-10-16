-- 1. Добавляем новые колонки
ALTER TABLE payments
    ADD COLUMN subscription_id UUID,
    ADD COLUMN payment_id      VARCHAR(64),
    ADD COLUMN paid_at         TIMESTAMP;

-- 2. Заполняем существующие записи
UPDATE payments SET payment_id = id::text   WHERE payment_id IS NULL;
UPDATE payments SET paid_at    = created_at WHERE paid_at    IS NULL;

-- 3. Обязательность и уникальность
ALTER TABLE payments ALTER COLUMN payment_id SET NOT NULL;
ALTER TABLE payments ALTER COLUMN paid_at    SET NOT NULL;

-- 4. Индексы
CREATE UNIQUE INDEX payments_payment_id_uniq      ON payments (payment_id);
CREATE INDEX        payments_subscription_id_idx  ON payments (subscription_id);