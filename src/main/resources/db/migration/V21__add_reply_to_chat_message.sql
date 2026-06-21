ALTER TABLE chat_message
    ADD COLUMN IF NOT EXISTS reply_to_id BIGINT,
    ADD COLUMN IF NOT EXISTS reply_to_content TEXT,
    ADD COLUMN IF NOT EXISTS reply_to_sender_name VARCHAR(255);

ALTER TABLE chat_message
    ADD CONSTRAINT fk_chat_message_reply
        FOREIGN KEY (reply_to_id) REFERENCES chat_message(id) ON DELETE SET NULL;