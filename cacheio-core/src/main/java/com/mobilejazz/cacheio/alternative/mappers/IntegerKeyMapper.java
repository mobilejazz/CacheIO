package com.mobilejazz.cacheio.alternative.mappers;

import com.mobilejazz.cacheio.alternative.Cache;

/**
 * Created by bmcgee on 28/02/2016.
 */
public class IntegerKeyMapper implements Cache.KeyMapper<Integer> {

    @Override
    public String toString(Integer key) {
        return key.toString();
    }

    @Override
    public Integer fromString(String str) {
        return Integer.parseInt(str);
    }
    
}
