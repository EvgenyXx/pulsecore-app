SELECT
    t.time,
    COUNT(*) AS games_count,
    AVG(tr.amount) AS avg_points,
    SUM(tr.amount) AS total_points,
    (AVG(tr.amount) * LN(COUNT(*))) AS productivity_score
FROM tournament_results tr
         JOIN tournament t ON t.id = tr.tournament_id
WHERE tr.player_id = :playerId
  AND t.time IS NOT NULL
  AND tr.date BETWEEN :start AND :end
GROUP BY t.time
ORDER BY productivity_score DESC;