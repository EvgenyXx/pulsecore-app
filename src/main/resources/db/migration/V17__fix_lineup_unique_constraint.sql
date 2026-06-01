ALTER TABLE lineup DROP CONSTRAINT IF EXISTS unique_lineup;
ALTER TABLE lineup DROP CONSTRAINT IF EXISTS lineup_league_time_date_key;

DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'lineup_league_time_date_hall_key'
              AND conrelid = 'lineup'::regclass
        ) THEN
            ALTER TABLE lineup ADD CONSTRAINT lineup_league_time_date_hall_key UNIQUE (league, time, date, hall);
        END IF;
    END $$;