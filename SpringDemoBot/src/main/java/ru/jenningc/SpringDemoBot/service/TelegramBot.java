package ru.jenningc.SpringDemoBot.service;

import com.vdurmont.emoji.EmojiParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.jenningc.SpringDemoBot.config.BotConfig;
import ru.jenningc.SpringDemoBot.model.joke.Joke;
import ru.jenningc.SpringDemoBot.model.joke.JokeRepository;
import ru.jenningc.SpringDemoBot.model.user.User;
import ru.jenningc.SpringDemoBot.model.user.UserRepository;
import ru.jenningc.SpringDemoBot.model.weather.WeatherApp;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JokeRepository jokeRepository;
    final BotConfig config;

    static final String HELP_TEXT =
            "Этот бот создан для демонстрации возможностей Spring.\n\n" +
            "Вы можете выполнять команды из главного меню слева или набрав команду.\n\n" +
            "Введите /start, чтобы увидеть приветственное сообщение\n\n" +
            "Введите /register, чтобы зарегистрировать себя\n\n" +
            "Введите /mydata, чтобы просмотреть данные, хранящиеся о Вас\n\n" +
            "Введите /deletedata, чтобы удалить хранящиеся о вас данные\n\n" +
            "Введите /help, чтобы снова увидеть это сообщение\n\n" +
            "Также можно воспользоваться кнопками ниже";


    static final String ERROR_TEXT = "Error occurred: ";
    static final String NOT_REGISTERED = "Вы еще не зарегистрированы.\n\n" +
            "Введите /register для регистрации.";
    public TelegramBot(BotConfig config) {
        this.config = config;
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "получить приветственное сообщение"));
        listOfCommands.add(new BotCommand("/register", "регистрация пользователя"));
        listOfCommands.add(new BotCommand("/mydata", "просмотреть данные, хранящиеся о Вас"));
        listOfCommands.add(new BotCommand("/deletedata", "удалить хранящиеся о Вас данные"));
        listOfCommands.add(new BotCommand("/help", "информация по использованию бота"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }
    @Override
    public void onUpdateReceived(Update update) {
        if(update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            if (messageText.contains("/send") && config.getOwnerId() == chatId) {
                sendMessageToAllUsers(messageText);
            } else {
                switch(messageText) {
                    case "/start":
                        startCommandReceived(chatId, message.getChat().getFirstName());
                        break;
                    case "/help":
                        sendHelpMessage(chatId);
                        break;
                    case "/mydata":
                        showUserData(message);
                        break;
                    case "/deletedata":
                        deleteUserData(message);
                        break;
                    case "/register":
                        registerUser(message);
                        break;
                    case "Погода":
                        System.out.println(WeatherApp.getLocationData("Tokyo"));
                        break;
                    case "Анекдот":
                        getJoke(message);
                        break;
                    default:
                        preparedAndSendMessage(chatId, "Отправьте что-либо из списка команд.");
                }
            }
        }
    }

    private void getJoke(Message message) {
        var chatId = message.getChatId();
        Random random = new Random();
        Optional<Joke> joke = jokeRepository.findById(random.nextInt(23));
        if (joke.isPresent()) {
            preparedAndSendMessage(chatId, joke.get().getJokeText());
        } else {
            preparedAndSendMessage(chatId, "Что-то поломалось, но мы обязательно это починим (не факт)");
        }
    }

    private void deleteUserData(Message message) {
        Optional<User> user = userRepository.findById(message.getChatId());
        var chatId = message.getChatId();
        if (user.isPresent()) {
            userRepository.deleteById(chatId);
            preparedAndSendMessage(chatId, "Ваши данные успешно удалены.");
        } else {
            preparedAndSendMessage(chatId, NOT_REGISTERED);
        }
    }
    private void showUserData(Message message) {
        Optional<User> user = userRepository.findById(message.getChatId());
        var chatId = message.getChatId();
        if (user.isPresent()) {
            preparedAndSendMessage(chatId, user.get().userData());
        } else {
            preparedAndSendMessage(chatId, NOT_REGISTERED);
        }
    }
    private void registerUser(Message message) {
        var chatId = message.getChatId();
        var chat = message.getChat();

        if (userRepository.findById(message.getChatId()).isEmpty()) {
            User user = new User();

            user.setChatId(chatId);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.setUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("User saved: " + user);

            preparedAndSendMessage(chatId, "Вы успешно зарегистрировались!");
        } else {
            preparedAndSendMessage(chatId, "Вы уже зарегистрированы! Введите /mydata для получения информации о Вас.");
        }

    }

    private void startCommandReceived(long chatId, String name) {

        String answer = EmojiParser.parseToUnicode("Привет, " + name + "! Хорошего тебе дня!" + " :hugs:\n\n" +
                "Введи команду /help для навигации по данному боту.");

        log.info("Replied to user " + name);

        preparedAndSendMessage(chatId, answer);
    }

    private void sendHelpMessage(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(TelegramBot.HELP_TEXT);

        message.setReplyMarkup(replyKeyboardMarkup());

        executeMessage(message);
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    private void executeMessage(SendMessage message) {
        try {
            execute(message);
        }
        catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }

    private void preparedAndSendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);
        message.setReplyMarkup(replyKeyboardMarkup());
        executeMessage(message);
    }

    private ReplyKeyboardMarkup replyKeyboardMarkup() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();

        List<KeyboardRow> keyboardRows = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();

        row.add("Погода");
        row.add("Анекдот");

        keyboardRows.add(row);

        keyboardMarkup.setKeyboard(keyboardRows);

        return keyboardMarkup;
    }

    private void sendMessageToAllUsers(String messageText) {
        var textToSend = EmojiParser.parseToUnicode(messageText.substring(messageText.indexOf(" ")));
        var users = userRepository.findAll();
        for (User user : users) {
            preparedAndSendMessage(user.getChatId(), textToSend);
        }
    }
}
