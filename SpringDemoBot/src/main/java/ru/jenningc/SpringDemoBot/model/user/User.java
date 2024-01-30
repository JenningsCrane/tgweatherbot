package ru.jenningc.SpringDemoBot.model.user;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Data
@Entity(name = "usersDataTable")
public class User {

    @Id
    private Long chatId;

    private String firstName;

    private String lastName;

    private String userName;

    private Timestamp registeredAt;


    @Override
    public String toString() {
        return "User{" +
                "chatId=" + chatId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", registeredAt=" + registeredAt +
                '}';
    }

    public String userData(){
        return  "ID чата: " + chatId + "\n\n" +
                "Ваше имя: " + (firstName == null ? "неизвестно" : firstName) + "\n\n" +
                "Ваша фамилия: " + (lastName == null ? "неизвестно" : lastName) + "\n\n" +
                "Ваше имя пользователя: " + (userName == null ? "неизвестно" : userName) + "\n\n" +
                "Дата вашей регистрации: " + registeredAt;
    }
}
