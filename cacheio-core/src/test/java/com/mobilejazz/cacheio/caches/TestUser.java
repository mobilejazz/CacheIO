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

package com.mobilejazz.cacheio.caches;


public class TestUser {

  private String email;
  private String firstName;
  private String lastName;
  private long version = 0L;

  public TestUser() {
  }

  public String getEmail() {
    return email;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public long getVersion() {
    return version;
  }

  public TestUser setEmail(String email) {
    this.email = email;
    return this;
  }

  public TestUser setFirstName(String firstName) {
    this.firstName = firstName;
    return this;
  }

  public TestUser setLastName(String lastName) {
    this.lastName = lastName;
    return this;
  }

  public TestUser setVersion(long version) {
    this.version = version;
    return this;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TestUser testUser = (TestUser) o;

    if (version != testUser.version) return false;
    if (email != null ? !email.equals(testUser.email) : testUser.email != null) return false;
    if (firstName != null ? !firstName.equals(testUser.firstName) : testUser.firstName != null) {
      return false;
    }
    return !(lastName != null ? !lastName.equals(testUser.lastName) : testUser.lastName != null);

  }

  @Override public int hashCode() {
    int result = email != null ? email.hashCode() : 0;
    result = 31 * result + (firstName != null ? firstName.hashCode() : 0);
    result = 31 * result + (lastName != null ? lastName.hashCode() : 0);
    result = 31 * result + (int) (version ^ (version >>> 32));
    return result;
  }

  @Override public String toString() {
    return "TestUser{" +
        "email='" + email + '\'' +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", version=" + version +
        '}';
  }
}
