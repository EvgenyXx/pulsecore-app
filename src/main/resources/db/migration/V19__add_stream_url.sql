-- V19__add_stream_url.sql
ALTER TABLE lineup ADD COLUMN IF NOT EXISTS stream_url VARCHAR(500);

UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1422' WHERE hall = '№1';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1208' WHERE hall = '№3';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1226' WHERE hall = '№4';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1341' WHERE hall = '№7';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1419' WHERE hall = '№10';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1420' WHERE hall = '№11';
UPDATE lineup SET stream_url = 'https://streamsport365.com/player/op/1635' WHERE hall = '№21';