-- liquibase formatted sql

-- changeset dbeliaev:1
CREATE TABLE notification_task (
    id SERIAL,
    chatId SERIAL,
    textMessage TEXT,
    timeReminder TIMESTAMP
)