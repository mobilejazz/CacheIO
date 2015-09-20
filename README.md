CacheIO
=============

CacheIO is a library to persist objects easier and define different caching strategies to be sure that you know when your information need to be updated.

Usage
-----

Create an instance of CacheIO. This is class configure the cache system.

```java
CacheIO cacheIO = CacheIO.with(getApplicationContext())
    .addLogging(true)
    .addDbName("cache.http")
    .addGson(GsonFactory.create())
    .build();
```

Now you can to get the CacheDataSource object that contains all the methods to persist and obtains your objects.

```java
CacheDataSource cacheDataSource = cacheIO.cacheDataSource();
```

Save a object:

```java
User userOne = new User();
userOne.setId(1);
userOne.setName("Jose Luis Franconetti");

// Create a cache entry object to save the User.
CacheEntry cacheEntryUserOne = CacheEntry.create("user.key.one" /*Key*/, User.class /*Type*/, userOne /*data*/);

cacheDataSource.persist(cacheEntryUserOne);
```

Obtain object:
```java
CacheEntry resultQueryUserOne = cacheDataSource.obtain("user.key.one");

User resultUser = (User) resultQueryUserOne.getValue();
```

See test cases in `src/test/` for more examples

Add it to your project
-------------------------------

Add CacheIO dependency to your build.gradle file

```xml
dependencies {
  compile 'com.github.mobilejazz:cacheio:0.2.1'
}
```

Do you want to contribute?
------------

Feel free to add any useful feature to the library, we will be glad to improve it :)

Libraries used in this project
---------------

* [SQLBrite] [1]
* [GSON] [2]

License
-------

    Copyright 2015 MobileJazz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: https://github.com/square/sqlbrite
[2]: https://github.com/google/gson