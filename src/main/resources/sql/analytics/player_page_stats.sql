WITH player_stats AS (
    SELECT p.name,
           pv.path,
           COUNT(*) AS cnt
    FROM page_views pv
             JOIN players p ON p.id = pv.player_id
    WHERE pv.created_at >= :since
      AND pv.email != 'evgenypavlov666@yandex.ru'
    GROUP BY p.name, pv.path
),
     player_totals AS (
         SELECT name, SUM(cnt) AS total
         FROM player_stats
         GROUP BY name
     ),
     all_total AS (
         SELECT SUM(cnt) AS grand_total FROM player_stats
     )
SELECT ps.name,
       STRING_AGG(ps.path || '=' || ps.cnt, '|' ORDER BY ps.cnt DESC) AS paths,
       pt.total,
       ROUND(pt.total * 100.0 / at.grand_total, 1) AS percent
FROM player_stats ps
         JOIN player_totals pt ON ps.name = pt.name
         CROSS JOIN all_total at
GROUP BY ps.name, pt.total, at.grand_total
ORDER BY pt.total DESC
LIMIT 50