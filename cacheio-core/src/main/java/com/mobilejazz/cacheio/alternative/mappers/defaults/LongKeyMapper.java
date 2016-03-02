package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.RxCache;

/**
 * Created by bmcgee on 28/02/2016.
 */
public class LongKeyMapper implements RxCache.KeyMapper<Long> {

    @Override
    public String toString(Long key) {
        return key.toString();
    }

    @Override
    public Long fromString(String str) {
        return Long.parseLong(str);
    }
    
}
