-- V36__add_city_to_top_players_view.sql

DROP VIEW IF EXISTS top_players_view;

CREATE VIEW top_players_view AS
SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    p.city,
    'WEEK' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('week', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('week', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league, p.city

UNION ALL

SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    p.city,
    'MONTH' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('month', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('month', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league, p.city

UNION ALL

SELECT
    p.id AS player_id,
    p.name,
    p.primary_league,
    p.city,
    'YEAR' AS period,
    COALESCE(SUM(CASE WHEN tr.date >= DATE_TRUNC('year', CURRENT_DATE)::date THEN tr.amount ELSE 0 END), 0) AS total,
    COUNT(CASE WHEN tr.date >= DATE_TRUNC('year', CURRENT_DATE)::date THEN 1 ELSE NULL END) AS tournaments
FROM players p
         LEFT JOIN tournament_results tr ON tr.player_id = p.id
WHERE p.primary_league IS NOT NULL
GROUP BY p.id, p.name, p.primary_league, p.city
ORDER BY period, total DESC;