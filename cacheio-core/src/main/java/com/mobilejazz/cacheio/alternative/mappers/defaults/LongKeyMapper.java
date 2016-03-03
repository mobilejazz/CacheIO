package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

public class LongKeyMapper implements KeyMapper<Long> {

    @Override
    public String toString(Long key) {
        return key.toString();
    }

    @Override
    public Long fromString(String str) {
        return Long.parseLong(str);
    }
    
}
