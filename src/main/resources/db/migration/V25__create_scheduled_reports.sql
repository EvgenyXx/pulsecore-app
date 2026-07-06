CREATE TABLE IF NOT EXISTS scheduled_reports (
                                                 id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                                 player_id UUID NOT NULL REFERENCES players(id) ON DELETE CASCADE,
                                                 date_from DATE NOT NULL,
                                                 date_to DATE NOT NULL,
                                                 scheduled_at TIMESTAMP NOT NULL,
                                                 status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                                                 created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Для шедулера: быстро найти PENDING отчёты, которые пора отправлять
CREATE INDEX idx_scheduled_reports_status_time
    ON scheduled_reports(status, scheduled_at)
    WHERE status = 'PENDING';

-- Для просмотра отчётов конкретного игрока
CREATE INDEX idx_scheduled_reports_player_id
    ON scheduled_reports(player_id);