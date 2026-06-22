CREATE TABLE IF NOT EXISTS hall_stream (
                                           hall VARCHAR(50) PRIMARY KEY,
                                           stream_url VARCHAR(500) NOT NULL
);

INSERT INTO hall_stream (hall, stream_url) VALUES
                                               ('№1', 'https://streamsport365.com/player/op/1422'),
                                               ('№3', 'https://streamsport365.com/player/op/1208'),
                                               ('№4', 'https://streamsport365.com/player/op/1226'),
                                               ('№7', 'https://streamsport365.com/player/op/1341'),
                                               ('№10', 'https://streamsport365.com/player/op/1419'),
                                               ('№11', 'https://streamsport365.com/player/op/1420'),
                                               ('№21', 'https://streamsport365.com/player/op/1635')
ON CONFLICT (hall) DO UPDATE SET stream_url = EXCLUDED.stream_url;