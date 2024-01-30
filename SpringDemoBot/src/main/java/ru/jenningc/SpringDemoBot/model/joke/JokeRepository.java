package ru.jenningc.SpringDemoBot.model.joke;

import org.springframework.data.repository.CrudRepository;

public interface JokeRepository extends CrudRepository<Joke, Integer> {

}
