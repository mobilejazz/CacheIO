/*
 * Copyright (C) 2016 Mobile Jazz
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.mobilejazz.cacheio.query;

public class PaginatedQuery implements Query {

  private final String id;
  private final int offset;
  private final int limit;

  public PaginatedQuery(String id, int offset, int limit) {
    this.id = id;
    this.offset = offset;
    this.limit = limit;
  }

  public int getOffset() {
    return offset;
  }

  public int getLimit() {
    return limit;
  }

  @Override public String getId() {
    return id;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PaginatedQuery that = (PaginatedQuery) o;

    if (offset != that.offset) return false;
    if (limit != that.limit) return false;
    return id.equals(that.id);

  }

  @Override public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + offset;
    result = 31 * result + limit;
    return result;
  }
}
