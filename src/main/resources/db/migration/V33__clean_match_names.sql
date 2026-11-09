UPDATE tournament_match
SET
    player1_name = LOWER(TRIM(REGEXP_REPLACE(player1_name, '\s+', ' ', 'g'))),
    player2_name = LOWER(TRIM(REGEXP_REPLACE(player2_name, '\s+', ' ', 'g'))),
    winner_name = LOWER(TRIM(REGEXP_REPLACE(winner_name, '\s+', ' ', 'g')));

CREATE INDEX IF NOT EXISTS idx_tournament_match_played_at ON tournament_match (played_at);