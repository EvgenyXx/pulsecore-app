-- V6__create_app_settings.sql
CREATE TABLE IF NOT EXISTS app_settings (
                                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    key VARCHAR(100) NOT NULL UNIQUE,
    value VARCHAR(500) NOT NULL
    );

INSERT INTO app_settings (key, value) VALUES
                                          ('price_1month', '99'),
                                          ('price_2months', '149')
    ON CONFLICT (key) DO NOTHING;