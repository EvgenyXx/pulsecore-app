-- V11__add_primary_league_and_views.sql
ALTER TABLE players ADD COLUMN IF NOT EXISTS primary_league VARCHAR(20);

CREATE OR REPLACE VIEW top_players_view AS
SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    'WEEK' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('week', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('week', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league

UNION ALL

SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    'MONTH' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('month', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('month', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league

UNION ALL

SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    'YEAR' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('year', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('year', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league
ORDER BY period, total DESC;