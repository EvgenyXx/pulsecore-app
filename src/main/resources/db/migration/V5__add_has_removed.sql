ALTER TABLE tournament_results ADD COLUMN IF NOT EXISTS has_removed BOOLEAN DEFAULT false;
ALTER TABLE tournament_results ALTER COLUMN has_removed SET NOT NULL;