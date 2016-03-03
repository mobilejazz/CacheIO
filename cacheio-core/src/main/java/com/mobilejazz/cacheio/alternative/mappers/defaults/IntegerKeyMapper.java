package com.mobilejazz.cacheio.alternative.mappers.defaults;


import com.mobilejazz.cacheio.alternative.mappers.KeyMapper;

public class IntegerKeyMapper implements KeyMapper<Integer> {

    @Override
    public String toString(Integer key) {
        return key.toString();
    }

    @Override
    public Integer fromString(String str) {
        return Integer.parseInt(str);
    }
    
}
