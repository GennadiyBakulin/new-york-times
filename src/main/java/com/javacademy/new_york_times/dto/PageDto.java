package com.javacademy.new_york_times.dto;

import java.util.List;
import lombok.Data;

@Data
public class PageDto<T> {

  private final List<T> content;
  private final Integer countPages;
  private final Integer currentPage;
  private final Integer maxPageSize;
  private final Integer size;
  private final Integer countAllNews;
}
