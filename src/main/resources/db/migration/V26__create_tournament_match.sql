CREATE TABLE tournament_match (
                                  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                  tournament_id BIGINT NOT NULL REFERENCES tournament(id),
                                  player1_name VARCHAR(255) NOT NULL,
                                  player2_name VARCHAR(255) NOT NULL,
                                  winner_name VARCHAR(255),
                                  stage VARCHAR(50) NOT NULL,
                                  score VARCHAR(50),
                                  played_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                  created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_tournament_match_tournament_id ON tournament_match(tournament_id);
CREATE INDEX idx_tournament_match_player1_name ON tournament_match(player1_name);
CREATE INDEX idx_tournament_match_player2_name ON tournament_match(player2_name);