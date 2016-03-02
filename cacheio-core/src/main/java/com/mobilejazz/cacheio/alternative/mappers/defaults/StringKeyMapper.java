package com.mobilejazz.cacheio.alternative.mappers.defaults;

import com.mobilejazz.cacheio.alternative.RxCache;

/**
 * Created by bmcgee on 28/02/2016.
 */
public class StringKeyMapper implements RxCache.KeyMapper<String> {

    @Override
    public String toString(String key) {
        return key;
    }

    @Override
    public String fromString(String str) {
        return str;
    }

}
