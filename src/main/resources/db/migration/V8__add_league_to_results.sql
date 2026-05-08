ALTER TABLE tournament_results ADD COLUMN IF NOT EXISTS league VARCHAR(50);

UPDATE tournament_results tr
SET league =
        CASE
            WHEN t.link LIKE '%liga-a%' OR t.link LIKE '%zhenskaja-liga-a%' THEN 'A'
            WHEN t.link LIKE '%liga-v%' OR t.link LIKE '%zhenskaja-liga-v%'
                OR t.link LIKE '%liga-b%' OR t.link LIKE '%zhenskaja-liga-b%' THEN 'B'
            WHEN t.link LIKE '%liga-s%' OR t.link LIKE '%zhenskaja-liga-s%'
                OR t.link LIKE '%liga-c%' OR t.link LIKE '%zhenskaja-liga-c%' THEN 'C'
            WHEN t.link LIKE '%liga-d%' OR t.link LIKE '%zhenskaja-liga-d%' THEN 'D'
            WHEN t.link LIKE '%superliga%' THEN 'SUPER_LEAGUE'
            ELSE 'UNKNOWN'
            END
FROM tournament t
WHERE tr.tournament_id = t.id AND tr.league IS NULL;