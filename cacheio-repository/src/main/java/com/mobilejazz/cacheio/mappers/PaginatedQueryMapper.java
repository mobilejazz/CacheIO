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

package com.mobilejazz.cacheio.mappers;

import com.mobilejazz.cacheio.query.PaginatedQuery;

public class PaginatedQueryMapper implements KeyMapper<PaginatedQuery> {

  private final static String DELIMITER = "_";

  @Override public String toString(PaginatedQuery model) {
    return String.valueOf(model.getId() + DELIMITER + model.getOffset())
        + DELIMITER
        + model.getLimit();
  }

  @Override public PaginatedQuery fromString(String str) {
    String[] tokens = str.split(DELIMITER);
    String id = tokens[0];
    String offset = tokens[1];
    String limit = tokens[2];
    return new PaginatedQuery(id, Integer.parseInt(offset), Integer.parseInt(limit));
  }
}
