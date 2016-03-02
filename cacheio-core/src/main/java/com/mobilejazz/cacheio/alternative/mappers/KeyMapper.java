package com.mobilejazz.cacheio.alternative.mappers;


interface KeyMapper<T> {

    String toString(T key);

    T fromString(String str);

}
