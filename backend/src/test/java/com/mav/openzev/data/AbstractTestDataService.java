package com.mav.openzev.data;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AbstractTestDataService {

  protected final TestDataManager testDataManager;

  public interface Customize<T> {
    void apply(T t);
  }
}
