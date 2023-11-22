package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @Autowired
    private NotificationTaskRepository repository;

    // create pattern
    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    private final static Pattern MESSAGE_PATTERN = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        //         info about updates
        logger.info("Processing update: {}", updates);
        //         starting bot
        for (Update update : updates) {
            String messageText = update.message().text();
            long chatId = update.message().chat().id();
            try {
                switch (messageText) {
                    case "/start":
                        // welcome message
                        startCommandReceived(chatId, update.message().chat().firstName());
                        logger.info("Welcome message was sent");
                        break;
                    default:
                        // reminder message
                        sendReminder(chatId, messageText);
                        logger.info("Reminder was saved");
                        break;
                }
                // catch error message
            } catch (NullPointerException e) {
                sendMessage(chatId, "Message format error");
                logger.info("Message format error");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void startCommandReceived(long chatId, String name) {
        // welcome message
        String answer = "Hi, " + name + ", write a reminder in the format: dd.mm.yyyy HH:MM text";
        sendMessage(chatId, answer);
    }


    private void sendMessage(long chatId, String textToSend) {
        // making message
        SendMessage sendMessage = new SendMessage(chatId, textToSend);
        // sending message
        SendResponse response = telegramBot.execute(sendMessage);
        if (!response.isOk()) {
            logger.error("Error occured during sending message to bot. Error: " + response.description());
        }
    }

    public void sendReminder(long chatId, String text) {
        // make finder
        Matcher matcher = MESSAGE_PATTERN.matcher(text);
        try {
            if (matcher.matches()) {
                String task = matcher.group(3);
                // make format
                LocalDateTime localDateTime = LocalDateTime.parse(matcher.group(1), DATE_TIME_FORMAT);
                // save task in DB
                repository.save(new NotificationTask(chatId, task, localDateTime));
                // successful
                sendMessage(chatId, "Reminder was saved!");
            } else {
                // error
                sendMessage(chatId, "Wrong format, try dd.mm.yyyy HH:MM text");
            }
            // catch error date
        } catch (DateTimeParseException e) {
            sendMessage(chatId, "Error date, try again");
            logger.info("Error date");
        }
    }

    @Scheduled(cron = "0 0/1 * * * *")
    public void findTask() {
        // find task by time reminder
        List<NotificationTask> tasks = repository.findAllByTimeReminder(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        logger.info("Method was started");
        for (NotificationTask task : tasks) {
            // sending task
            sendMessage(task.getChatId(), task.getTextMessage());
            logger.info("Task was sending");
        }
    }
}
