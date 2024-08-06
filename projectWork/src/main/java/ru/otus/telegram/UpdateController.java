package ru.otus.telegram;

import java.util.function.Function;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Log4j2
@Component
public class UpdateController {
    private TelegramBot telegramBot;
    private final HandlerProvider handlerProvider;
    private final InlineKeyboardMaker inlineKeyboardMaker;
    private final ReplyKeyboardMaker replyKeyboardMaker;

    public UpdateController(
            HandlerProvider handlerProvider,
            InlineKeyboardMaker inlineKeyboardMaker,
            ReplyKeyboardMaker replyKeyboardMaker) {
        this.handlerProvider = handlerProvider;
        this.inlineKeyboardMaker = inlineKeyboardMaker;
        this.replyKeyboardMaker = replyKeyboardMaker;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;

        log.info("register bot");
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramBot);
        } catch (TelegramApiException e) {
            log.error("Error occurred: " + e.getMessage());
        }
    }

    public void processUpdate(Update update) {
        if (update.hasMessage()) {
            processMessage(update);
        } else if (update.hasCallbackQuery()) {
            processCallbackQuery(update);
        }
    }

    private void processMessage(Update update) {
        String requestMessage = update.getMessage().getText();

        Function<Update, String> handler = handlerProvider.getHandler(requestMessage, update);
        String responseMessageText = handler.apply(update);

        SendMessage sendMessage = prepareMessage(requestMessage, responseMessageText, update);
        sendMessage(sendMessage);
    }

    private void processCallbackQuery(Update update) {
        Function<Update, String> handler = handlerProvider.getHandler("/add_favourite", update);
        String responseMessageText = handler.apply(update);

        SendMessage sendMessage = prepareMessage("", responseMessageText, update);
        sendMessage(sendMessage);
    }

    private SendMessage prepareMessage(String requestMessage, String messageText, Update update) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        SendMessage sendMessage = new SendMessage(String.valueOf(chatId), messageText);

        if ("/start".equals(requestMessage)) {
            sendMessage.setReplyMarkup(replyKeyboardMaker.getMainMenuKeyboard());
        }

        if (HandlerProvider.isFilmDetailsMessage(requestMessage)) {
            Function<Update, String> handler = handlerProvider.getHandler("/is_favourite", update);
            boolean isFilmFavourite = handler.apply(update) != null;
            String buttonText = isFilmFavourite ? "Удалить из избранного" : "Добавить в избранное";
            InlineKeyboardMarkup inlineKeyboardMarkup =
                    inlineKeyboardMaker.getInlineMessageButtons(buttonText, requestMessage);
            sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        }

        return sendMessage;
    }

    private void sendMessage(SendMessage sendMessage) {
        try {
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
