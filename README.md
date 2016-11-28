![CacheIO](https://raw.githubusercontent.com/mobilejazz/metadata/master/images/banners/mobile-jazz-cacheio-android.jpg)

CacheIO
=======

CacheIO is a library to store objects easier. 

## Gradle Dependency

Add this in your root `build.gradle` file (**not** your module `build.gradle` file):

```gradle
allprojects {
	repositories {
		...
		maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
	}
}
```


#### Core

The core module contains all the major classes of this library. You can store / update / remove objects with the core.

```xml
dependencies {
    // ... other dependencies here
    compile 'com.mobilejazz.cacheio:core:1.0.1-SNAPSHOT'
}
```


#### Repository

The repository module contains a full implementation of the repository pattern using the core module. 

```xml
dependencies {
    // ... other dependencies here
    compile 'com.mobilejazz.cacheio:repository:1.0.1-SNAPSHOT'
}
```

#### Gson Serializer

The gson serializer module implement a value mapper using gson as serializer instead of java serializer. 

```xml
dependencies {
    // ... other dependencies here
    compile 'com.mobilejazz.cacheio:gson-serializer:1.0.1-SNAPSHOT'
}
```

## Using CacheIO

*For a working implementation of this project see the `sample/` folder.*

### 1. Initialize CacheIO instance

#### 1.1 Basic CacheIO instance

To create a basic cacheIO instance you must to specify the executor where the operation will be executed and the database name.

```java
CacheIO cacheIO = CacheIO.with(this)
        .executor(Executors.newSingleThreadExecutor()) // Executor where all the operation will be executed
        .identifier("database-name") // Database name
        .build();
```

#### 1.2 Configuring ValueMapper

CacheIO use java deserialization by the default but you can create your own `ValueMapper`. We have implemented a `GsonValueMapper` to use gson as deserializer instead of java. 
 
```java
CacheIO cacheIO = CacheIO.with(this)
        .executor(Executors.newSingleThreadExecutor()) // Executor where all the operation will be executed
        .identifier("database-name") // Database name
        .setValueMapper(new GsonValueMapper(new Gson())) // Using GSON as value mapper
        .build();
``` 

#### 1.3 Configuring KeyMappers

The library injects by the default all the mappers required for primitive types, but if you use your objects as keys so you will need to create you own `KeyMapper`.
 
You can see a example of `KeyMapper` in the class `LongKeyMapper`

```java
CacheIO cacheIO = CacheIO.with(this)
        .executor(Executors.newSingleThreadExecutor()) // Executor where all the operation will be executed
        .identifier("database-name") // Database name
        .setValueMapper(new GsonValueMapper(new Gson())) // Using GSON as value mapper
        .setKeyMapper(User.class, new UserKeyMapper()) // Adding a custom keymapper to use the object User as key
        .build();
``` 

#### 1.3 Configuring Versioning

To do

### 2. Start to use CacheIO

CacheIO is built on top of RxJava and we built three ways to work with the library, with Observables, Futures or just in a sync way.  

So, when you have the CacheIO instance you can get your different cache instances.
 
#### 2.1 Observable

First, you need to create a new RxCache with a specific key type and key value to get the `RxCache` instance.

```java
RxCache<String, User> rxCache = cacheIO.newRxCache(String.class, User.class);
``` 

**CRUD Operations**

PUT
```java
// Store object
rxCache.put("user.key", new User(), 1 /*expireIn*/, TimeUnit.DAYS);

Map<String, User> values = new HashMap<>();
values.put("user.key.one", new User());
values.put("user.key.two", new User());

// Store a map with multiples values
rxCache.putAll(values, 1 /*expireIn*/, TimeUnit.DAYS);
``` 

GET

```java
// Get a specific user.
Single<User> userSingle = rxCache.get("user.key");

// Get multiples users.
Single<Map<String, User>> usersSingle = rxCache.getAll(Arrays.asList("user.key.one", "user.key.two"));
``` 

REMOVE

```java
// Remove a single user
rxCache.remove("user.key");

// Remove multiples users
rxCache.removeAll(Arrays.asList("user.key.one", "user.key.two"));
``` 

#### 2.2 Future

First, you need to create a new FutureCache with a specific key type and key value to get the `FutureCache` instance.

```java
FutureCache<String, User> futureCache = cacheIO.newFutureCache(String.class, User.class);
``` 

**CRUD Operations**

PUT
```java
// Store object
futureCache.put("user.key", new User(), 1 /*expireIn*/, TimeUnit.DAYS);

Map<String, User> values = new HashMap<>();
values.put("user.key.one", new User());
values.put("user.key.two", new User());

// Store a map with multiples values
futureCache.putAll(values, 1 /*expireIn*/, TimeUnit.DAYS);
``` 

GET

```java
// Get a specific user.
Future<User> userFuture = futureCache.get("user.key");

// Get multiples users.
Future<Map<String, User>> futureUsers = futureCache.getAll(Arrays.asList("user.key.one", "user.key.two"));
``` 

REMOVE

```java
// Remove a single user
futureCache.remove("user.key");

// Remove multiples users
futureCache.removeAll(Arrays.asList("user.key.one", "user.key.two"));
``` 

#### 2.3 Sync

First, you need to create a new SyncCache with a specific key type and key value to get the `SyncCache` instance.

```java
SyncCache<String, User> syncCache = cacheIO.newSyncCache(String.class, User.class);
``` 

**CRUD Operations**

PUT
```java
// Store object
syncCache.put("user.key", new User(), 1 /*expireIn*/, TimeUnit.DAYS);

Map<String, User> values = new HashMap<>();
values.put("user.key.one", new User());
values.put("user.key.two", new User());

// Store a map with multiples values
syncCache.putAll(values, 1 /*expireIn*/, TimeUnit.DAYS);
``` 

GET

```java
// Get a specific user.
User user = syncCache.get("user.key");

// Get multiples users.
Map<String, User> users = syncCache.getAll(Arrays.asList("user.key.one", "user.key.two"));
``` 

REMOVE

```java
// Remove a single user
syncCache.remove("user.key");

// Remove multiples users
syncCache.removeAll(Arrays.asList("user.key.one", "user.key.two"));
```

### 3. Using the CacheIO Repository

If you want to have more features, you will need to use the repository module to have it.

When you start to use the repository module you will have another public contract that it's a wrapper on top of the cacheio-core.
 
**Public Contract**

```java
public interface RxRepository<Id, M extends HasId<Id>, Q extends Query> {

  Single<List<M>> find(Q query);

  Single<M> findById(Id id);

  Single<List<M>> put(Q query, List<M> models);

  Single<M> put(M model);

  Single<Id> removeById(Id id);

  Single<Collection<Id>> removeByQuery(Q query);

}
```
You can see that we have the concept of `Query` concept but it's just a key concept in a abstract way. For example, we have implemented inside the repository library the pagination query that it's just a wrapper of the pagination offset and limit (`PaginatedQuery`) but you can create your own `Query` version. Maybe you need a specific way to do a query for a specific object.

Currently with just have implemented one repository that it's base on Strings but you can use it if you want to have a key type repository. It's called `StringKeyedRxRepository` and you can start to use it.

### 3.1 Initializing the repository

You need to have two different caches to be able to use the Repository module, one for the models cache and another for the query cache. So you will need to have two RxCache instances first and then you can instantiate the `StringKeyedRxRepository` class or your own implementation if you want. 
   
```java
RxCache<String, User> userCache = cacheIO.newRxCache(String.class, User.class);
RxCache<DefaultQuery, StringList> queryCache = cacheIO.newRxCache(DefaultQuery.class, StringList.class);

RxRepository<String, User, DefaultQuery> repository = new StringKeyedRxRepository.Builder<User, DefaultQuery>()
        .setCache(cache)
        .setQueryCache(queryCache)
        .build();
```

### 3.2 CRUD Operations

PUT
```java
User user = new User();

// Store a single user
repository.put(user);

// Create the query object
PaginatedQuery paginatedQuery = new PaginatedQuery("paginated.query", 0, 10);

// Store a list of users referenced to a query object
repository.put(paginatedQuery, Arrays.asList(user, user, user));
``` 

GET

```java
// Get a specific user.
Single<User> userById = repository.findById(user.getId());

// Get multiples users by a paginated query.
Single<List<User>> usersByQuery = repository.find(paginatedQuery);
``` 

REMOVE

```java
// Remove a single user
repository.removeById(user.getId());

// Remove multiples users by a paginated query
repository.removeByQuery(paginatedQuery);
```


## Project Maintainer

This open source project is maintained by [Jose Luis Franconetti](https://github.com/joselufo).

## License

    Copyright 2016 Mobile Jazz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
