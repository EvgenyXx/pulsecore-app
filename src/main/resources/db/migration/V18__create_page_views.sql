-- V18__create_page_views.sql
CREATE TABLE page_views (
                            id BIGSERIAL PRIMARY KEY,
                            player_id UUID,
                            email VARCHAR(255) NOT NULL,
                            path VARCHAR(500) NOT NULL,
                            method VARCHAR(10) NOT NULL,
                            user_agent TEXT,
                            ip VARCHAR(45),
                            created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_page_views_email ON page_views(email);
CREATE INDEX idx_page_views_created_at ON page_views(created_at DESC);