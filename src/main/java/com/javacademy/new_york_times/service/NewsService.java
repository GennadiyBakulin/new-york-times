package com.javacademy.new_york_times.service;

import com.javacademy.new_york_times.dto.NewsDto;
import com.javacademy.new_york_times.dto.PageDto;
import com.javacademy.new_york_times.entity.NewsEntity;
import com.javacademy.new_york_times.mapper.NewsMapper;
import com.javacademy.new_york_times.repository.NewsRepository;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NewsService {

  private final int PAGE_SIZE = 10;

  private final NewsRepository newsRepository;
  private final NewsMapper newsMapper;

  public void save(NewsDto dto) {
    newsRepository.save(newsMapper.toEntity(dto));
  }

  /**
   * Переписать этот метод
   */
  public ResponseEntity<?> findAll(Integer pageNumber) {
    List<NewsEntity> allNews = newsRepository.findAll();
    if (pageNumber == null) {
      List<NewsDto> newsDtoList = newsMapper.toDtos(allNews);
      return ResponseEntity.ok(newsDtoList);
    }
    List<NewsEntity> newsEntityList = allNews.stream()
        .sorted(Comparator.comparing(NewsEntity::getNumber))
        .skip((long) PAGE_SIZE * pageNumber)
        .limit(PAGE_SIZE)
        .toList();
    List<NewsDto> newsDtoList = newsMapper.toDtos(newsEntityList);
    int totalPages = allNews.size() / PAGE_SIZE;
    PageDto<NewsDto> pageDto = new PageDto<>(newsDtoList, totalPages, pageNumber, PAGE_SIZE,
        newsDtoList.size(), allNews.size());
    return ResponseEntity.ok(pageDto);
  }

  public NewsDto findByNumber(Integer number) {
    return newsMapper.toDto(newsRepository.findByNumber(number).orElseThrow());
  }

  public boolean deleteByNumber(Integer number) {
    return newsRepository.deleteByNumber(number);
  }

  public void update(NewsDto dto) {
    newsRepository.update(newsMapper.toEntity(dto));
  }

  public String getNewsText(Integer newsNumber) {
    return newsRepository.findByNumber(newsNumber).map(NewsEntity::getText).orElseThrow();
  }

  public String getNewsAuthor(Integer newsNumber) {
    return newsRepository.findByNumber(newsNumber).map(NewsEntity::getAuthor).orElseThrow();
  }
}
