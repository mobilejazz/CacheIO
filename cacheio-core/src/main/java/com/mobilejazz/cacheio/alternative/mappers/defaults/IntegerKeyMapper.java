package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.RxCache;

/**
 * Created by bmcgee on 28/02/2016.
 */
public class IntegerKeyMapper implements RxCache.KeyMapper<Integer> {

    @Override
    public String toString(Integer key) {
        return key.toString();
    }

    @Override
    public Integer fromString(String str) {
        return Integer.parseInt(str);
    }
    
}
