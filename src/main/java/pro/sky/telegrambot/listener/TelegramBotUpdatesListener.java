package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

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
        logger.info("Processing update: {}", updates);
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
        String answer = "Hi, " + name + ", let's start!";
        sendMessage(chatId, answer);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
    }

}
