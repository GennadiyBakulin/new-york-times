package com.javacademy.new_york_times.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PageDto <T> {
  private List<T> content;
  private Integer countPages;
  private Integer currentPage;
  private Integer maxPageSize;
  private Integer size;
}
