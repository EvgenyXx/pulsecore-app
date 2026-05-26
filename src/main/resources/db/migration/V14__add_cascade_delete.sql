ALTER TABLE tournament_results
    DROP CONSTRAINT tournament_results_player_id_fkey,
    ADD CONSTRAINT tournament_results_player_id_fkey
        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

ALTER TABLE player_notification
    DROP CONSTRAINT player_notification_player_id_fkey,
    ADD CONSTRAINT player_notification_player_id_fkey
        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

ALTER TABLE push_subscriptions
    DROP CONSTRAINT fk_push_player,
    ADD CONSTRAINT fk_push_player
        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;

ALTER TABLE subscription
    DROP CONSTRAINT subscription_player_id_fkey,
    ADD CONSTRAINT subscription_player_id_fkey
        FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE;