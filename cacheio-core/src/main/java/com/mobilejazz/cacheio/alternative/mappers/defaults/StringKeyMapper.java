package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

public class StringKeyMapper implements KeyMapper<String> {

    @Override
    public String toString(String key) {
        return key;
    }

    @Override
    public String fromString(String str) {
        return str;
    }

}
