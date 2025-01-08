package com.javacademy.new_york_times.controller;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.repository.NewsRepository;
import com.javacademy.new_york_times.service.NewsService;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@AutoConfigureMockMvc
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
class NewsControllerTest {

  @Autowired
  private NewsService service;
  @Autowired
  private NewsRepository repository;
  private final RequestSpecification requestSpecification = new RequestSpecBuilder()
      .setBasePath("/news")
      .log(LogDetail.ALL)
      .build()
      .contentType("application/json");
  private final ResponseSpecification responseSpecification = new ResponseSpecBuilder()
      .log(LogDetail.ALL)
      .build();

  @Test
  @DisplayName("Успешное создание новости")
  void createNewsSuccess() {
    NewsDto expected = NewsDto.builder()
        .title("Создание новой новости")
        .text("Создание новой новости для проверки теста")
        .author("Create-Test")
        .build();

    RestAssured.given(requestSpecification)
        .body(expected)
        .post()
        .then()
        .statusCode(201);

    NewsDto result = service.findByNumber(repository.findAll().size());
    Assertions.assertEquals(expected.getTitle(), result.getTitle());
    Assertions.assertEquals(expected.getText(), result.getText());
    Assertions.assertEquals(expected.getAuthor(), result.getAuthor());
  }

  @Test
  @DisplayName("Успешное обновление новости")
  void updateNewsSuccess() {
    int numberLastNews = repository.findAll().size();
    NewsDto expected = NewsDto.builder()
        .number(numberLastNews)
        .title("Обновление новости")
        .text("Обновление существующей новости для проверки теста")
        .author("Update-Test")
        .build();

    NewsDto newsBeforeUpdate = service.findByNumber(numberLastNews);
    Assertions.assertNotEquals(expected.getTitle(), newsBeforeUpdate.getTitle());
    Assertions.assertNotEquals(expected.getText(), newsBeforeUpdate.getText());
    Assertions.assertNotEquals(expected.getAuthor(), newsBeforeUpdate.getAuthor());

    RestAssured.given(requestSpecification)
        .body(expected)
        .patch()
        .then()
        .spec(responseSpecification)
        .statusCode(201);

    NewsDto newsAfterUpdate = service.findByNumber(numberLastNews);
    Assertions.assertEquals(expected.getTitle(), newsAfterUpdate.getTitle());
    Assertions.assertEquals(expected.getText(), newsAfterUpdate.getText());
    Assertions.assertEquals(expected.getAuthor(), newsAfterUpdate.getAuthor());
  }

  @Test
  @DisplayName("Успешное получение текста новости")
  void getNewsTextSuccess() {
    int numberLastNews = repository.findAll().size();
    String expected = service.getNewsText(numberLastNews);
    String result = RestAssured.given(requestSpecification)
        .pathParam("id", numberLastNews)
        .get("/{id}/text")
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .asPrettyString();

    Assertions.assertEquals(expected, result);
  }

  @Test
  @DisplayName("Успешное получение автора новости")
  void getNewsAuthorSuccess() {
    int numberLastNews = repository.findAll().size();
    String expected = service.getNewsAuthor(numberLastNews);
    String result = RestAssured.given(requestSpecification)
        .pathParam("id", numberLastNews)
        .get("/{id}/author")
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .asPrettyString();

    Assertions.assertEquals(expected, result);
  }

  @Test
  @DisplayName("Успешное удаление новости")
  void deleteNewsSuccess() {
    int countNewsBeforeDelete = repository.findAll().size();
    Boolean result = RestAssured.given(requestSpecification)
        .pathParam("id", countNewsBeforeDelete)
        .delete("/{id}")
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .body()
        .as(Boolean.class);

    int countNewsAfterDelete = repository.findAll().size();

    Assertions.assertTrue(result);
    Assertions.assertEquals(1, countNewsBeforeDelete - countNewsAfterDelete);
  }

  @Test
  @DisplayName("Успешное получение новости")
  void getNewsSuccess() {
    int numberLastNews = repository.findAll().size();
    NewsDto expectedNews = service.findByNumber(numberLastNews);
    NewsDto resultNews = RestAssured.given(requestSpecification)
        .pathParam("id", numberLastNews)
        .get("/{id}")
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .body()
        .as(NewsDto.class);

    Assertions.assertEquals(expectedNews.getTitle(), resultNews.getTitle());
    Assertions.assertEquals(expectedNews.getText(), resultNews.getText());
    Assertions.assertEquals(expectedNews.getAuthor(), resultNews.getAuthor());
  }

  @Test
  @DisplayName("Успешное получение всех новостей")
  void getAllNewsSuccess() {
    int countAllNews = repository.findAll().size();
    PageDto<NewsDto> resultPageDto = RestAssured.given(requestSpecification)
        .get()
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .body()
        .as(new TypeRef<>() {
        });

    Assertions.assertEquals(10, resultPageDto.getContent().size());
    Assertions.assertEquals(100, resultPageDto.getCountPages());
    Assertions.assertEquals(0, resultPageDto.getCurrentPage());
    Assertions.assertEquals(10, resultPageDto.getMaxPageSize());
    Assertions.assertEquals(countAllNews, resultPageDto.getCountAllNews());
  }

  @Test
  @DisplayName("Успешное получение всех новостей в рамках пагинации")
  void getAllNewsSuccessPagination() {
    int countAllNews = repository.findAll().size();
    PageDto<NewsDto> resultPageDto = RestAssured.given(requestSpecification)
        .queryParam("pageNumber", 1)
        .get()
        .then()
        .spec(responseSpecification)
        .statusCode(200)
        .extract()
        .body()
        .as(new TypeRef<>() {
        });

    Assertions.assertEquals(10, resultPageDto.getContent().size());
    Assertions.assertEquals(100, resultPageDto.getCountPages());
    Assertions.assertEquals(1, resultPageDto.getCurrentPage());
    Assertions.assertEquals(10, resultPageDto.getMaxPageSize());
    Assertions.assertEquals(countAllNews, resultPageDto.getCountAllNews());
  }
}
