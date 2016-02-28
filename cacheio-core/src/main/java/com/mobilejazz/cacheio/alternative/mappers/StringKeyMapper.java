package com.mobilejazz.cacheio.alternative.mappers;

import com.mobilejazz.cacheio.alternative.Cache;

/**
 * Created by bmcgee on 28/02/2016.
 */
public class StringKeyMapper implements Cache.KeyMapper<String> {

    @Override
    public String toString(String key) {
        return key;
    }

    @Override
    public String fromString(String str) {
        return str;
    }

}
