-- ===== Категории =====

CREATE TABLE category (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    active      BOOLEAN NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- ===== Продукты =====

CREATE TABLE product (
    id           BIGSERIAL PRIMARY KEY,
    name         VARCHAR(200) NOT NULL,
    description  TEXT,
    category_id  BIGINT NOT NULL REFERENCES category(id),
    brand        VARCHAR(100),
    price        NUMERIC(10, 2) NOT NULL,
    stock        INTEGER NOT NULL,
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_active ON product(active);

-- ===== Фото продуктов =====

CREATE TABLE product_image (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    sort_order  INTEGER,
    is_main     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_product_image_product ON product_image(product_id);

-- ===== Корзина =====

CREATE TABLE cart (
    id          BIGSERIAL PRIMARY KEY,
    user_id     UUID NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_cart_user UNIQUE (user_id)
);

CREATE INDEX idx_cart_user ON cart(user_id);

-- ===== Товары в корзине =====

CREATE TABLE cart_item (
    id          BIGSERIAL PRIMARY KEY,
    cart_id     BIGINT NOT NULL REFERENCES cart(id) ON DELETE CASCADE,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT uk_cart_item_cart_product UNIQUE (cart_id, product_id)
);

CREATE INDEX idx_cart_item_cart ON cart_item(cart_id);
CREATE INDEX idx_cart_item_product ON cart_item(product_id);

-- ===== Заказы =====

CREATE TABLE orders (
    id                       BIGSERIAL PRIMARY KEY,
    user_id                  UUID NOT NULL,
    status                   VARCHAR(20) NOT NULL,
    payment_method           VARCHAR(20) NOT NULL,
    payment_status           VARCHAR(20) NOT NULL,
    delivery_method          VARCHAR(20) NOT NULL,

    customer_first_name      VARCHAR(100),
    customer_last_name       VARCHAR(100),
    customer_middle_name     VARCHAR(100),

    delivery_phone           VARCHAR(30) NOT NULL,
    delivery_city            VARCHAR(100) NOT NULL,
    delivery_street          VARCHAR(300) NOT NULL,
    comment                  TEXT,

    total_price              NUMERIC(10, 2) NOT NULL,
    yookassa_payment_id      VARCHAR(100),

    created_at               TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at               TIMESTAMP
);

CREATE INDEX idx_order_user ON orders(user_id);
CREATE INDEX idx_order_status ON orders(status);
CREATE INDEX idx_order_created ON orders(created_at);

-- ===== Позиции заказа =====

CREATE TABLE order_item (
    id                  BIGSERIAL PRIMARY KEY,
    order_id            BIGINT NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id          BIGINT NOT NULL,
    product_name        VARCHAR(200) NOT NULL,
    product_brand       VARCHAR(100),
    product_image_url   VARCHAR(500),
    product_price       NUMERIC(10, 2) NOT NULL,
    quantity            INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_item_order ON order_item(order_id);