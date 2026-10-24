ALTER TABLE players ADD COLUMN name_normalized VARCHAR(255);


UPDATE players
SET name_normalized = (
    SELECT string_agg(word, ' ' ORDER BY word)
    FROM unnest(regexp_split_to_array(lower(trim(name)), '\s+')) AS word
)
WHERE name_normalized IS NULL;

ALTER TABLE players ALTER COLUMN name_normalized SET NOT NULL;
CREATE UNIQUE INDEX uk_player_name_normalized ON players(name_normalized);