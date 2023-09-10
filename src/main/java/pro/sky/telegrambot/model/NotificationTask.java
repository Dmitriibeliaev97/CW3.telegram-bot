package pro.sky.telegrambot.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private long chatId;
    private String textMessage;
    private LocalDateTime timeReminder;

    public NotificationTask(long id, long chatId, String textMessage, LocalDateTime timeReminder) {
        this.id = id;
        this.chatId = chatId;
        this.textMessage = textMessage;
        this.timeReminder = timeReminder;
    }

    public NotificationTask(String task, long chatId, LocalDateTime localDateTime) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id == that.id && chatId == that.chatId && Objects.equals(textMessage, that.textMessage) && Objects.equals(timeReminder, that.timeReminder);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, chatId, textMessage, timeReminder);
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", chatId=" + chatId +
                ", textMessage='" + textMessage + '\'' +
                ", timeReminder=" + timeReminder +
                '}';
    }
}

