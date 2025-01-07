package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.service.NewsService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Сделать 7 операций внутри контроллера. 1. Создание новости. Должно чистить кэш. 2. Удаление
 * новости по id. Должно чистить кэш. 3. Получение новости по id. Должно быть закэшировано. 4.
 * Получение всех новостей (новости должны отдаваться порциями по 10 штук). Должно быть
 * закэшировано. 5. Обновление новости по id. Должно чистить кэш. 6. Получение текста конкретной
 * новости. 7. Получение автора конкретной новости.
 */
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

  private final NewsService service;

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void createNews(@RequestBody NewsDto newsDto) {
    service.save(newsDto);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public boolean deleteNews(@PathVariable Integer id) {
    return service.deleteByNumber(id);
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public NewsDto findByNumberNews(@PathVariable Integer id) {
    return service.findByNumber(id);
  }

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public List<NewsDto> findAllNews() {
    return service.findAll();
  }

  @PatchMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void updateNews(@RequestBody NewsDto newsDto) {
    service.update(newsDto);
  }

  @GetMapping("/text/{id}")
  @ResponseStatus(HttpStatus.OK)
  public String getNewsText(@PathVariable Integer id) {
    return service.getNewsText(id);
  }

  @GetMapping("/author/{id}")
  @ResponseStatus(HttpStatus.OK)
  public String getNewsAuthor(@PathVariable Integer id) {
    return service.getNewsAuthor(id);
  }
}
