package ru.jenningc.SpringDemoBot.model.joke;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity(name = "jokesDataTable")
public class Joke {
    @Id
    private Integer jokeId;

    private String jokeText;

}
