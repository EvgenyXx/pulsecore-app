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
    active       BOOLEAN NOT NULL DEFAULT TRUE,
    created_at   TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP
);

CREATE INDEX idx_product_category ON product(category_id);
CREATE INDEX idx_product_active ON product(active);

-- ===== Цвета продуктов =====

CREATE TABLE product_color (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    color       VARCHAR(100),
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT product_color_unique UNIQUE (product_id, color)
);

CREATE INDEX idx_product_color_product ON product_color(product_id);

-- ===== Размеры продуктов =====

CREATE TABLE product_size (
    id          BIGSERIAL PRIMARY KEY,
    product_id  BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    size        VARCHAR(50),
    sort_order  INTEGER DEFAULT 0,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT product_size_unique UNIQUE (product_id, size)
);

CREATE INDEX idx_product_size_product ON product_size(product_id);

-- ===== Варианты продуктов =====

CREATE TABLE product_variant (
    id           BIGSERIAL PRIMARY KEY,
    product_id   BIGINT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    color_id     BIGINT REFERENCES product_color(id) ON DELETE CASCADE,
    size_id      BIGINT REFERENCES product_size(id) ON DELETE CASCADE,
    stock        INTEGER NOT NULL DEFAULT 0,
    price_delta  NUMERIC(10, 2) DEFAULT 0
);

CREATE INDEX idx_product_variant_product ON product_variant(product_id);
CREATE INDEX idx_product_variant_color ON product_variant(color_id);
CREATE INDEX idx_product_variant_size ON product_variant(size_id);

CREATE UNIQUE INDEX pv_unique_full
    ON product_variant (product_id, color_id, size_id)
    WHERE color_id IS NOT NULL AND size_id IS NOT NULL;

CREATE UNIQUE INDEX pv_unique_color_only
    ON product_variant (product_id, color_id)
    WHERE color_id IS NOT NULL AND size_id IS NULL;

CREATE UNIQUE INDEX pv_unique_size_only
    ON product_variant (product_id, size_id)
    WHERE color_id IS NULL AND size_id IS NOT NULL;

CREATE UNIQUE INDEX pv_unique_none
    ON product_variant (product_id)
    WHERE color_id IS NULL AND size_id IS NULL;

-- ===== Фото цветов =====

CREATE TABLE product_image (
    id          BIGSERIAL PRIMARY KEY,
    color_id    BIGINT NOT NULL REFERENCES product_color(id) ON DELETE CASCADE,
    url         VARCHAR(500) NOT NULL,
    sort_order  INTEGER,
    is_main     BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE INDEX idx_product_image_color ON product_image(color_id);

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
    variant_id  BIGINT NOT NULL REFERENCES product_variant(id) ON DELETE CASCADE,
    quantity    INTEGER NOT NULL CHECK (quantity > 0),
    CONSTRAINT uk_cart_item_cart_variant UNIQUE (cart_id, variant_id)
);

CREATE INDEX idx_cart_item_cart ON cart_item(cart_id);
CREATE INDEX idx_cart_item_variant ON cart_item(variant_id);

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
    variant_id          BIGINT,
    product_name        VARCHAR(200) NOT NULL,
    product_brand       VARCHAR(100),
    variant_size        VARCHAR(50),
    variant_color       VARCHAR(100),
    product_image_url   VARCHAR(500),
    product_price       NUMERIC(10, 2) NOT NULL,
    quantity            INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE INDEX idx_order_item_order ON order_item(order_id);