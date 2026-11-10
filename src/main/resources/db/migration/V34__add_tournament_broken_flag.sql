ALTER TABLE tournament
    ADD COLUMN IF NOT EXISTS broken boolean NOT NULL DEFAULT false;

CREATE INDEX IF NOT EXISTS idx_tournament_broken_true_date
    ON tournament (date)
    WHERE broken = true;