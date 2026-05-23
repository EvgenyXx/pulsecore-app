CREATE TABLE IF NOT EXISTS push_subscriptions (
                                                  id SERIAL PRIMARY KEY,
                                                  player_id UUID NOT NULL,
                                                  endpoint VARCHAR(512) NOT NULL,
                                                  p256dh TEXT NOT NULL,
                                                  auth TEXT NOT NULL,
                                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                                  CONSTRAINT fk_push_player FOREIGN KEY (player_id) REFERENCES players(id)
);

CREATE INDEX IF NOT EXISTS idx_push_player ON push_subscriptions(player_id);