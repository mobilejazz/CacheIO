package com.mobilejazz.cacheio.model;

import java.io.Serializable;

public class UserTestModelSerializable implements Serializable {

  private int id;
  private String name;

  public UserTestModelSerializable() {
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
