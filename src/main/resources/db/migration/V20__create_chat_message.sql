-- V20__create_chat_message.sql
CREATE TABLE IF NOT EXISTS chat_message (
                                            id BIGSERIAL PRIMARY KEY,
                                            lineup_id BIGINT NOT NULL REFERENCES lineup(id),
                                            player_id UUID NOT NULL REFERENCES players(id),
                                            player_name VARCHAR(255) NOT NULL,
                                            message TEXT NOT NULL,
                                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_chat_lineup ON chat_message(lineup_id, created_at);