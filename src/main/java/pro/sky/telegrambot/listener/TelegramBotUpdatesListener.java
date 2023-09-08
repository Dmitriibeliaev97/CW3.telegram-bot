package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    @Autowired
    private TelegramBot telegramBot;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        // info about updates
        logger.info("Processing update: {}", updates);
        // starting bot
        for (Update update : updates) {
            String massageText = update.message().text();
            long chatId = update.message().chat().id();
            if (massageText.equals("/start")) {
                startCommandReceived(chatId, update.message().chat().firstName());
            } else {
                sendMessage(chatId, "Sorry, command was not recognized");
            }
        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void startCommandReceived(long chatId, String name) {
        // welcome message
        String answer = "Hi, " + name + ", let's start!";
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

}
